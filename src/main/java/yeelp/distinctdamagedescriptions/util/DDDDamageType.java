package yeelp.distinctdamagedescriptions.util;

import yeelp.distinctdamagedescriptions.capability.IDistribution;

/**
 * Basic skeleton of a DDD damage type.
 * @author Yeelp
 *
 */
public interface DDDDamageType
{
	public enum Type
	{
		PHYSICAL,
		SPECIAL;
	}
	/**
	 * Get the name of this type, with the "ddd_" prefix
	 * @return the type name, with the "ddd_" prefix.
	 */
	public String getTypeName();
	
	/**
	 * Get the base damage distribution for this type
	 * @return and IDistribution that distributes the damage to the type returned by {@link #getTypeName()}
	 */
	public IDistribution getBaseDistribution();
	
	/**
	 * Get the type of this damage
	 * @return a Type enum, that is either PHYSICAL or SPECIAL
	 */
	public Type getType();
}


