package io.github.jbaero.nbt;

import com.laytonsmith.abstraction.MCChunk;
import com.laytonsmith.abstraction.MCEntity;
import com.laytonsmith.abstraction.MCItemStack;
import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.abstraction.MCOfflinePlayer;
import com.laytonsmith.abstraction.blocks.MCBlock;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.*;
import com.laytonsmith.core.exceptions.CRE.CREIOException;
import com.laytonsmith.core.exceptions.CRE.CREIllegalArgumentException;
import com.laytonsmith.core.exceptions.CRE.CREPluginInternalException;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTList;
import me.dpohvar.powernbt.api.NBTManager;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utils, 2/25/2016 1:46 AM
 *
 * @author jb_aero
 */
public class Utils
{
	private enum MyNBTType
	{
		NULL,
		COMPOUND,
		LIST,
		BYTE,
		SHORT,
		INT,
		LONG,
		FLOAT,
		DOUBLE,
		BYTEARRAY,
		INTARRAY,
		STRING,
		UNHANDLED;

		static MyNBTType fromString(String search)
		{
			try {
				return valueOf(search);
			} catch (IllegalArgumentException iae) {
				return UNHANDLED;
			}
		}
	}

	// For writing, converts CommandHelper associative array to PowerNBT NBTCompound
	public static NBTCompound ArrayToCompound(CArray source, Target t)
	{
		Map<String, Object> converted = new HashMap<>();

		for (String key : source.stringKeySet())
		{
			converted.put(key, IdentArrayToObject(Static.getArray(source.get(key, t), t), t));
		}

		return new NBTCompound(converted);
	}

	// For reading, converts PowerNBT NBTCompound to CommandHelper associative array
	public static CArray CompoundToArray(NBTCompound compound, Target t)
	{
		CArray ret = CArray.GetAssociativeArray(t);

		if (compound != null) {
			for (Map.Entry<String, Object> entry : compound.entrySet()) {
				ret.set(entry.getKey(), ObjectToIdentArray(entry.getValue(), t), t);
			}
		}

		return ret;
	}

	public static Object ConstructToObject(Construct source, MyNBTType type, Target t)
	{
		switch (type)
		{
			case BYTE:
				return Static.getInt8(source, t);
			case SHORT:
				return Static.getInt16(source, t);
			case INT:
				return Static.getInt32(source, t);
			case LONG:
				return Static.getInt(source, t);
			case FLOAT:
				return Static.getDouble32(source, t);
			case DOUBLE:
				return Static.getDouble(source, t);
			case BYTEARRAY:
				return Static.getByteArray(source, t).asByteArrayCopy();
			case INTARRAY:
				CArray ca = Static.getArray(source, t);
				int[] val = new int[(int) ca.size()];
				for (int i = 0; i < ca.size(); i++)
				{
					val[i] = Static.getInt32(ca.get(i, t), t);
				}
			case STRING:
				return source.val();
			case COMPOUND:
				return ArrayToCompound(Static.getArray(source, t), t);
			case LIST:
				return list(Static.getArray(source, t), t);
			case NULL:
				return null;
			case UNHANDLED:
				throw new CREIllegalArgumentException("An item in an array passed to an NBT function had type "
						+ type.name() + ", which is invalid.", t);
			default:
				throw new CREPluginInternalException("If you are seeing this message please report it at "
						+ "https://github.com/jb-aero/CHNBT along with the steps needed to reproduce.", t);
		}
	}

	public static Object IdentArrayToObject(CArray item, Target t)
	{
		return ConstructToObject(item.get("value", t), MyNBTType.fromString(item.get("type", t).val()), t);
	}

	public static CArray ObjectToIdentArray(Object obj, Target t)
	{
		CArray ret = CArray.GetAssociativeArray(t);
		MyNBTType type;
		Construct value;

		// Compound should not contain null values, this is here for safety
		if (obj == null) {
			type = MyNBTType.NULL;
			value = CNull.NULL;
		} else if (obj instanceof NBTCompound) {
			type = MyNBTType.COMPOUND;
			value = CompoundToArray((NBTCompound) obj, t);
		} else if (obj instanceof NBTList) {
			type = MyNBTType.LIST;
			value = list((NBTList) obj, t);
		} else if (obj instanceof Byte) {
			type = MyNBTType.BYTE;
			value = new CInt(obj.toString(), t);
		} else if (obj instanceof Short) {
			type = MyNBTType.SHORT;
			value = new CInt(obj.toString(), t);
		} else if (obj instanceof Integer) {
			type = MyNBTType.INT;
			value = new CInt(obj.toString(), t);
		} else if (obj instanceof Long) {
			type = MyNBTType.LONG;
			value = new CInt(obj.toString(), t);
		} else if (obj instanceof Float) {
			type = MyNBTType.FLOAT;
			value = new CDouble(obj.toString(), t);
		} else if (obj instanceof Double) {
			type = MyNBTType.DOUBLE;
			value = new CDouble(obj.toString(), t);
		} else if (obj instanceof byte[]) {
			type = MyNBTType.BYTEARRAY;
			value = CByteArray.wrap((byte[]) obj, t);
		} else if (obj instanceof int[]) {
			CArray sub = new CArray(t);
			for (int i : (int[]) obj) {
				sub.push(new CInt(i, t), t);
			}
			type = MyNBTType.INTARRAY;
			value = sub;
		} else if (obj instanceof String) {
			type = MyNBTType.STRING;
			value = new CString((String) obj, t);
		} else {
			type = MyNBTType.UNHANDLED;
			value = new CString(obj.getClass().getSimpleName(), t);
		}

		ret.set("type", type.name());
		ret.set("value", value, t);
		return ret;
	}

	public static NBTList list(CArray source, Target t)
	{
		CArray content = Static.getArray(source.get("content", t), t);
		MyNBTType subtype = MyNBTType.fromString(source.get("subtype", t).val());
		List<Object> ret = new ArrayList<>();

		for (Construct c : content.asList())
		{
			ret.add(ConstructToObject(c, subtype, t));
		}

		return new NBTList(ret);
	}

	public static CArray list(NBTList list, Target t)
	{
		CArray ret = CArray.GetAssociativeArray(t);
		CArray content = new CArray(t);
		MyNBTType subtype;

		switch (list.getType()) {
			case 0:
				subtype = MyNBTType.NULL;
				break;
			case 11: // int arrays
				subtype = MyNBTType.INTARRAY;
				for (Object obj : list.toArrayList()) {
					CArray sub = new CArray(t);
					for (int i : (int[]) obj) {
						sub.push(new CInt(i, t), t);
					}
					content.push(sub, t);
				}
				break;
			case 10: // compounds
				subtype = MyNBTType.COMPOUND;
				for (Object obj : list.toArrayList()) {
					NBTCompound toWrite;
					if (obj instanceof HashMap) {
						toWrite = new NBTCompound();
						toWrite.merge((Map) obj);
					} else {
						toWrite = (NBTCompound) obj;
					}
					content.push(CompoundToArray(toWrite, t), t);
				}
				break;
			case 9: // lists
				subtype = MyNBTType.LIST;
				for (Object obj : list.toArrayList()) {
					content.push(list((NBTList) obj, t), t);
				}
				break;
			case 7: // byte array
				subtype = MyNBTType.BYTEARRAY;
				for (Object obj : list.toArrayList()) {
					content.push(CByteArray.wrap((byte[]) obj, t), t);
				}
				break;
			default:
				Params type = listType(list.getType(), t, list.get(0));
				subtype = type.subtype;
				for (Object obj : list.toArrayList()) {
					try {
						content.push(type.type.getConstructor(type.argument, Target.class).newInstance(obj, t), t);
					} catch (InstantiationException | IllegalAccessException
							| InvocationTargetException | NoSuchMethodException e) {
						throw new CREPluginInternalException(e.getMessage(), t, e);
					}
				}
		}

		ret.set("subtype", subtype.name());
		ret.set("content", content, t);
		return ret;
	}

	public static Params listType(byte type, Target t, Object o)
	{
		try {
			return listType(type, t);
		} catch (CREPluginInternalException e) {
			throw new CREPluginInternalException(e.getMessage() + ", class: " + o.getClass().getSimpleName(), t);
		}
	}

	public static Params listType(byte type, Target t)
	{
		MyNBTType subtype;
		switch (type) {
			case 1:
				return new Params(CInt.class, MyNBTType.BYTE, long.class);
			case 2:
				return new Params(CInt.class, MyNBTType.SHORT, long.class);
			case 3:
				return new Params(CInt.class, MyNBTType.INT, long.class);
			case 4:
				return new Params(CInt.class, MyNBTType.LONG, long.class);
			case 5:
				return new Params(CDouble.class, MyNBTType.FLOAT, double.class);
			case 6:
				return new Params(CDouble.class, MyNBTType.DOUBLE, double.class);
			case 8:
				return new Params(CString.class, MyNBTType.STRING, String.class);
			default:
				throw new CREPluginInternalException("Unexpected data tag: " + type, t);
		}
	}

	private static class Params
	{
		Class<? extends Construct> type;
		MyNBTType subtype;
		Class argument;

		Params(Class<? extends Construct> type, MyNBTType subtype, Class argument) {
			this.type = type;
			this.subtype = subtype;
			this.argument = argument;
		}
	}

	public static CArray readBlock(MCBlock block, Target t) {
		return CompoundToArray(NBTManager.getInstance().read((Block) block.getHandle()), t);
	}

	public static CArray readBlock(MCLocation loc, Target t) {
		return readBlock(loc.getBlock(), t);
	}

	public static CArray readChunk(MCChunk chunk, Target t) {
		return CompoundToArray(NBTManager.getInstance().read((Chunk) chunk.getHandle()), t);
	}

	public static CArray readChunk(MCLocation loc, Target t) {
		return readChunk(loc.getChunk(), t);
	}

	public static CArray readEntity(MCEntity ent, Target t) {
		return CompoundToArray(NBTManager.getInstance().read((Entity) ent.getHandle()), t);
	}

	public static CArray readUser(MCOfflinePlayer player, Target t) {
		return CompoundToArray(NBTManager.getInstance().readOfflinePlayer((OfflinePlayer) player.getHandle()), t);
	}

	public static CArray readItem(MCItemStack item, Target t) {
		return CompoundToArray(NBTManager.getInstance().read((ItemStack) item.getHandle()), t);
	}

	public static CArray readFile(File file, Target t) {
		try {
			return CompoundToArray(NBTManager.getInstance().readCompressed(file), t);
		} catch (FileNotFoundException e) {
			throw new CREIOException("Could not read file " + file.getAbsolutePath(), t);
		} catch (Exception e2) {
			throw new CREPluginInternalException(e2.getMessage(), t);
		}
	}
}
