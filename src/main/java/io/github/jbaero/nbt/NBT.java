package io.github.jbaero.nbt;

import com.laytonsmith.PureUtilities.Common.StringUtils;
import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.abstraction.*;
import com.laytonsmith.abstraction.enums.MCEquipmentSlot;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.ObjectGenerator;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CNull;
import com.laytonsmith.core.constructs.CVoid;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.CRE.*;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import com.laytonsmith.core.functions.InventoryManagement;

/**
 * NBT, 2/25/2016 1:35 AM
 *
 * @author jb_aero
 */
public class NBT {

	public static String docs()	{
		return "TBD";
	}

	public static abstract class NBTFunction extends AbstractFunction {

		@Override
		public boolean isRestricted() {
			return true;
		}

		@Override
		public Boolean runAsync() {
			return false;
		}

		@Override
		public String getName() {
			return this.getClass().getSimpleName();
		}
	}

	@api
	public static class nbt_read_file extends NBTFunction {

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREIOException.class, CREPluginInternalException.class};
		}

		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			return Utils.readFile(Static.GetFileFromArgument(args[0].val(), env, t, null), t);
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{1};
		}

		@Override
		public String docs() {
			return "array {fileLocation} Reads the NBT data of an arbitrary file.";
		}

		@Override
		public Version since() {
			return new SimpleVersion(0, 1, 0);
		}
	}

	@api
	public static class nbt_write_file extends NBTFunction {

		@Override
		public Class<? extends CREThrowable>[] thrown()
		{
			return new Class[]{CREIOException.class};
		}

		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException
		{
			Utils.writeFile(Static.GetFileFromArgument(args[0].val(), env, t, null), Static.getArray(args[1], t), t);
			return CVoid.VOID;
		}

		@Override
		public Version since()
		{
			return new SimpleVersion(0, 4, 0);
		}

		@Override
		public Integer[] numArgs()
		{
			return new Integer[]{2};
		}

		@Override
		public String docs()
		{
			return "void {fileLocation, NBTArray} Writes NBT data to an arbitrary file.";
		}
	}

	@api
	public static class nbt_read_block extends NBTFunction {

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREFormatException.class, CREPluginInternalException.class};
		}

		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			return Utils.readBlock(ObjectGenerator.GetGenerator().location(args[0], null, t), t);
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{1};
		}

		@Override
		public String docs() {
			return "array {locationArray} Reads the NBT data of a single block.";
		}

		@Override
		public Version since() {
			return new SimpleVersion(0, 1, 0);
		}
	}

	@api
	public static class nbt_write_block extends NBTFunction {

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREFormatException.class, CREPluginInternalException.class, CRECastException.class};
		}

		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Utils.writeBlock(ObjectGenerator.GetGenerator().location(args[0], null, t), Static.getArray(args[1], t), t);
			return CVoid.VOID;
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{2};
		}

		@Override
		public String docs() {
			return "void {locationArray, NBTArray} Writes NBT data to a single block.";
		}

		@Override
		public Version since() {
			return new SimpleVersion(0, 1, 0);
		}
	}

	@api
	public static class nbt_read_entity extends NBTFunction {

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREBadEntityException.class, CREPluginInternalException.class};
		}

		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			return Utils.readEntity(Static.getEntity(args[0], t), t);
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{1};
		}

		@Override
		public String docs() {
			return "array {uuid} Reads the NBT data of an entity.";
		}

		@Override
		public Version since() {
			return new SimpleVersion(0, 1, 0);
		}
	}

	@api
	public static class nbt_write_entity extends NBTFunction {

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREBadEntityException.class, CREPluginInternalException.class, CRECastException.class};
		}

		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Utils.writeEntity(Static.getEntity(args[0], t), Static.getArray(args[1], t), t);
			return CVoid.VOID;
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{2};
		}

		@Override
		public String docs() {
			return "void {uuid, NBTArray} Writes NBT data to an entity.";
		}

		@Override
		public Version since() {
			return new SimpleVersion(0, 4, 0);
		}
	}

	@api
	public static class nbt_read_player extends NBTFunction {

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPluginInternalException.class};
		}

		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			return Utils.readUser(Static.GetUser(args[0], t), t);
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{1};
		}

		@Override
		public String docs() {
			return "array {uuid or exact name} Reads the NBT data of a player file.";
		}

		@Override
		public Version since() {
			return new SimpleVersion(0, 1, 0);
		}
	}

	@api
	public static class nbt_write_player extends NBTFunction {

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPluginInternalException.class, CRECastException.class};
		}

		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			Utils.writeUser(Static.GetUser(args[0], t), Static.getArray(args[1], t), t);
			return CVoid.VOID;
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{2};
		}

		@Override
		public String docs() {
			return "void {uuid or exact name, NBTArray} Writes NBT data to a player file.";
		}

		@Override
		public Version since() {
			return new SimpleVersion(0, 4, 0);
		}
	}

	@api
	public static class nbt_read_inventory_item extends NBTFunction
	{

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPluginInternalException.class, CREFormatException.class, CRERangeException.class};
		}

		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {

			MCInventory inv = GetInventory(args[0], t);

			try {
				return Utils.readItem(inv.getItem(Static.getInt32(args[1], t)), t);
			}
			catch (ArrayIndexOutOfBoundsException e) {
				throw new CRERangeException("Index out of bounds for the given inventory type.", t);
			}
		}

		@Override
		public Version since() {
			return new SimpleVersion(0, 2, 0);
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{2};
		}

		@Override
		public String docs() {
			return "array {locationArray, slotNumber | uuid, slotNumber} Reads the NBT data of an item in an inventory."
					+ " The first argument is either the coordinates of a block, or the uuid of an entity.";
		}
	}

	// TODO make this public in CH
	private static MCInventory GetInventory(Construct specifier, Target t) {
		MCInventory inv;
		if(specifier instanceof CArray) {
			MCLocation l = ObjectGenerator.GetGenerator().location(specifier, null, t);
			inv = StaticLayer.GetConvertor().GetLocationInventory(l);
			if(inv == null) {
				throw new CREIllegalArgumentException("The location specified is not capable of having an inventory.", t);
			}
			return inv;
		}
		if(specifier.val().length() == 36 || specifier.val().length() == 32) {
			try {
				MCEntity entity = Static.getEntity(specifier, t);
				inv = StaticLayer.GetConvertor().GetEntityInventory(entity);
				if(inv == null) {
					throw new CREIllegalArgumentException("The entity specified is not capable of having an inventory.", t);
				}
				return inv;
			} catch (CREFormatException iae) {
				// not a UUID
			}
		}
		inv = InventoryManagement.VIRTUAL_INVENTORIES.get(specifier.val());
		if(inv == null) {
			throw new CREIllegalArgumentException("An inventory for \"" + specifier.val() + "\" does not exist.", t);
		}
		return inv;
	}

	@api
	public static class nbt_write_inventory_item extends NBTFunction
	{

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPluginInternalException.class, CREFormatException.class,
					CRERangeException.class, CRECastException.class};
		}

		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {

			MCInventory inv = GetInventory(args[0], t);

			try {
				Utils.writeItem(inv.getItem(Static.getInt32(args[1], t)), Static.getArray(args[2], t), t);
			}
			catch (ArrayIndexOutOfBoundsException e) {
				throw new CRERangeException("Index out of bounds for the given inventory type.", t);
			}
			return CVoid.VOID;
		}

		@Override
		public Version since() {
			return new SimpleVersion(0, 4, 0);
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{3};
		}

		@Override
		public String docs() {
			return "void {locationArray/uuid, slotNumber, NBTArray} Writes NBT data to an item in an inventory."
					+ " The first argument is either the coordinates of a block, or the uuid of an entity.";
		}
	}

	@api
	public static class nbt_read_equipment_item extends NBTFunction {

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPluginInternalException.class, CREFormatException.class};
		}

		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {

			MCEntityEquipment eq = Static.getLivingEntity(args[0], t).getEquipment();
			MCEquipmentSlot slot;
			try {
				slot = MCEquipmentSlot.valueOf(args[1].val());
			}
			catch (IllegalArgumentException e) {
				throw new CREFormatException("Invalid slot name.", t);
			}

			MCItemStack stack = null;
			switch (slot) {
				case BOOTS:
					stack = eq.getBoots();
					break;
				case CHESTPLATE:
					stack = eq.getChestplate();
					break;
				case HELMET:
					stack = eq.getHelmet();
					break;
				case WEAPON:
					stack = eq.getWeapon();
					break;
				case LEGGINGS:
					stack = eq.getLeggings();
					break;
				case OFF_HAND:
					stack = eq.getItemInOffHand();
					break;
			}

			return Utils.readItem(stack, t);
		}

		@Override
		public Version since() {
			return new SimpleVersion(0, 2, 0);
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{2};
		}

		@Override
		public String docs() {
			return "array {uuid, slotName} Reads the NBT data of an item equipped on an entity. Slot name must be "
					+ StringUtils.Join(MCEquipmentSlot.values(), ", ", ", or ", " or ");
		}
	}

	@api
	public static class nbt_write_equipment_item extends NBTFunction {

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPluginInternalException.class, CREFormatException.class, CRECastException.class};
		}

		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {

			MCEntityEquipment eq = Static.getLivingEntity(args[0], t).getEquipment();
			MCEquipmentSlot slot;
			try {
				slot = MCEquipmentSlot.valueOf(args[1].val());
			}
			catch (IllegalArgumentException e) {
				throw new CREFormatException("Invalid slot name.", t);
			}

			MCItemStack stack = null;
			switch (slot) {
				case BOOTS:
					stack = eq.getBoots();
					break;
				case CHESTPLATE:
					stack = eq.getChestplate();
					break;
				case HELMET:
					stack = eq.getHelmet();
					break;
				case WEAPON:
					stack = eq.getWeapon();
					break;
				case LEGGINGS:
					stack = eq.getLeggings();
					break;
				case OFF_HAND:
					stack = eq.getItemInOffHand();
					break;
			}

			Utils.writeItem(stack, Static.getArray(args[2], t), t);
			return CVoid.VOID;
		}

		@Override
		public Version since() {
			return new SimpleVersion(0, 4, 0);
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{3};
		}

		@Override
		public String docs() {
			return "array {uuid, slotName, NBTArray} Reads the NBT data of an item equipped on an entity. Slot name must be "
					+ StringUtils.Join(MCEquipmentSlot.values(), ", ", ", or ", " or ");
		}
	}
}
