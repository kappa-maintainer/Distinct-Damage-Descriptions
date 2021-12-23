package yeelp.distinctdamagedescriptions.registries;

import java.util.Collection;

import javax.annotation.Nullable;

import yeelp.distinctdamagedescriptions.config.ModConfig;

/**
 * Base registry for DDD
 * 
 * @author Yeelp
 *
 * @param <T> the type registered in the registry
 */
public interface IDDDRegistry<T> extends Iterable<T> {
	/**
	 * Initialize this registry
	 */
	void init();

	/**
	 * Register an object
	 * 
	 * @param obj
	 */
	default void register(T obj) {
		register(ModConfig.core.suppressRegistrationInfo, obj);
	}

	/**
	 * Register an object
	 * 
	 * @param suppressOutput true if log output should be suppressed for this
	 *                       registration.
	 * @param obj
	 */
	void register(boolean suppressOutput, T obj);

	/**
	 * Register a collection of objects
	 * 
	 * @param objs
	 */
	default void registerAll(@SuppressWarnings("unchecked") T... objs) {
		registerAll(ModConfig.core.suppressRegistrationInfo, objs);
	}

	/**
	 * Register a collection of objects
	 * 
	 * @param suppressOutput true if log output should be suppressed
	 * @param objs
	 */
	void registerAll(boolean suppressOutput, @SuppressWarnings("unchecked") T... objs);

	/**
	 * Check if an object is registered
	 * 
	 * @param obj
	 * @return true if registered
	 */
	boolean isRegistered(T obj);

	/**
	 * Get a value from the registry
	 * 
	 * @param key
	 * @return The value from the registry, null if not present.
	 */
	@Nullable
	T get(String key);
	
	/**
	 * Get all registered values
	 * @return all registered values
	 */
	Collection<T> getAll();
}
