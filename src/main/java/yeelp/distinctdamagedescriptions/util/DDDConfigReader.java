package yeelp.distinctdamagedescriptions.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.util.Tuple;
import yeelp.distinctdamagedescriptions.DistinctDamageDescriptions;
import yeelp.distinctdamagedescriptions.ModConfig;
import yeelp.distinctdamagedescriptions.api.DDDDamageType;
import yeelp.distinctdamagedescriptions.api.impl.DDDBuiltInDamageType;
import yeelp.distinctdamagedescriptions.capability.impl.ArmorDistribution;
import yeelp.distinctdamagedescriptions.capability.impl.DamageDistribution;
import yeelp.distinctdamagedescriptions.capability.impl.ShieldDistribution;
import yeelp.distinctdamagedescriptions.init.config.DDDConfigurations;
import yeelp.distinctdamagedescriptions.init.config.IDDDConfiguration;
import yeelp.distinctdamagedescriptions.registries.DDDRegistries;
import yeelp.distinctdamagedescriptions.util.lib.InvariantViolationException;
import yeelp.distinctdamagedescriptions.util.lib.NonNullMap;

/**
 * Read configuration entries from Config
 * 
 * @author Yeelp
 *
 */
public final class DDDConfigReader {
	public static void readConfig() {
		readBasicConfigEntry(ModConfig.dmg.mobBaseDmg, 0.0f, DDDConfigurations.mobDamage, (map -> new DamageDistribution(map)));
		DistinctDamageDescriptions.info("Mob damage loaded!");
		readBasicConfigEntry(ModConfig.dmg.itemBaseDamage, 0.0f, DDDConfigurations.items, (map -> new DamageDistribution(map)));
		DistinctDamageDescriptions.info("Weapon damage loaded!");
		readBasicConfigEntry(ModConfig.resist.armorResist, 0.0f, DDDConfigurations.armors, (map -> new ArmorDistribution(map)));
		DistinctDamageDescriptions.info("Armor Resistances loaded!");
		readBasicConfigEntry(ModConfig.resist.shieldResist, 0.0f, DDDConfigurations.shields, (map -> new ShieldDistribution(map)));
		DistinctDamageDescriptions.info("Shield Distributions loaded!");
		readMobResistances();
		DistinctDamageDescriptions.info("Mob Resistances loaded!");
		readProjectileDamage();
		DistinctDamageDescriptions.info("Projectile Damage loaded!");
	}

	private static void readMobResistances() {
		for(String s : ModConfig.resist.mobBaseResist) {
			if(s.endsWith(";")) {
				DistinctDamageDescriptions.err("Config entry " + s + " shouldn't end in a semicolon! Please remove!");
				continue;
			}
			String[] arr = s.split(";");
			try {
				readResistVals(arr[0], arr);
			}
			catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
				DistinctDamageDescriptions.warn(s + " isn't a valid entry! Ignoring...");
			}
		}
		if(!ModConfig.resist.playerResists.equals("[];[];0;0")) {
			readResistVals("player", ("player;"+ModConfig.resist.playerResists).split(";"));
		}
	}
	
	private static void readResistVals(String key, String[] vals) {
		NonNullMap<DDDDamageType, Float> map = buildMap(0.0f, parseListOfTuples(vals[1]));
		Set<DDDDamageType> immunities = parseImmunitiesFromArray(vals[2]);
		DDDConfigurations.mobResists.put(key, new MobResistanceCategories(map, immunities, Float.parseFloat(vals[3]), Float.parseFloat(vals[4])));
	}

	private static void readProjectileDamage() {
		for(String s : ModConfig.dmg.projectileDamageTypes) {
			String[] arr = tryPut(0.0f, s, DDDConfigurations.projectiles, (map -> new DamageDistribution(map)));
			if(arr == null || arr.length == 2) {
				continue;
			}
			for(String str : arr[2].split(",")) {
				DDDConfigurations.projectiles.registerItemProjectilePair(str.trim(), arr[0]);
			}
		}
	}

	private static <T> void readBasicConfigEntry(String[] arr, float defaultVal, IDDDConfiguration<T> config, Function<NonNullMap<DDDDamageType, Float>, T> constructor) {
		for(String s : arr) {
			tryPut(defaultVal, s, config, constructor);
		}
	}

	private static <T> String[] tryPut(float defaultVal, String s, IDDDConfiguration<T> config, Function<NonNullMap<DDDDamageType, Float>, T> constructor) {
		if(s.endsWith(";")) {
			DistinctDamageDescriptions.err("Config entry " + s + " shouldn't end in a semicolon! Please remove!");
			return null;
		}
		String[] contents = s.split(";");
		try {
			config.put(contents[0], constructor.apply(buildMap(defaultVal, parseListOfTuples(contents[1]))));
			return contents;
		}
		catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
			DistinctDamageDescriptions.warn(s + " isn't a valid entry! Ignoring...");
		}
		catch(UnsupportedOperationException f) // This occurs in NonNullMap, when the damage type isn't registered
		{
			DistinctDamageDescriptions.err(s + " references an invalid damage type! Perhaps it was spelled incorrectly?");
			throw f;
		}
		catch(InvariantViolationException g) {
			DistinctDamageDescriptions.err(s + " is formatted incorrectly! Read the config!");
			throw g;
		}
		return null;
	}

	public static Iterable<Tuple<DDDDamageType, Float>> parseListOfTuples(String s) {
		// s is of the form [(t, a), (t, a), ... , (t, a)]
		if(s.equals("[]")) {
			return Collections.emptyList();
		}
		List<Tuple<DDDDamageType, Float>> lst = new LinkedList<Tuple<DDDDamageType, Float>>();
		for(String str : s.substring(2, s.length() - 2).split("\\),(?:\\s?)\\(")) {
			String[] temp = str.split(",");
			lst.add(new Tuple<DDDDamageType, Float>(parseDamageType(temp[0].trim()), Float.parseFloat(temp[1].trim())));
		}
		return lst;
	}

	public static <K, V> NonNullMap<K, V> buildMap(V defaultVal, Iterable<Tuple<K, V>> mappings) {
		if(mappings == null) {
			return new NonNullMap<K, V>(defaultVal);
		}
		NonNullMap<K, V> map = new NonNullMap<K, V>(defaultVal);
		for(Tuple<K, V> t : mappings) {
			map.put(t.getFirst(), t.getSecond());
		}
		return map;
	}

	private static Set<DDDDamageType> parseImmunitiesFromArray(String s) {
		Set<DDDDamageType> set = new HashSet<DDDDamageType>();
		if(s.equals("") || s.equals("[]")) {
			return set;
		}
		String[] arr = s.substring(1, s.length() - 1).split(",");
		for(String str : arr) {
			set.add(parseDamageType(str.trim()));
		}
		return set;
	}

	private static DDDDamageType parseDamageType(String s) {
		switch(s) {
			case "s":
				return DDDBuiltInDamageType.SLASHING;
			case "p":
				return DDDBuiltInDamageType.PIERCING;
			case "b":
				return DDDBuiltInDamageType.BLUDGEONING;
			default:
				return DDDRegistries.damageTypes.get(s);
		}
	}
}
