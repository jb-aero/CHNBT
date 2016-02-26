package io.github.jbaero.nbt;

import com.laytonsmith.abstraction.MCChunk;
import com.laytonsmith.abstraction.MCEntity;
import com.laytonsmith.abstraction.MCItemStack;
import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.abstraction.MCOfflinePlayer;
import com.laytonsmith.abstraction.blocks.MCBlock;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CByteArray;
import com.laytonsmith.core.constructs.CDouble;
import com.laytonsmith.core.constructs.CInt;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.exceptions.CRE.CREIOException;
import com.laytonsmith.core.exceptions.CRE.CREPluginInternalException;
import me.dpohvar.powernbt.api.NBTCompound;
import me.dpohvar.powernbt.api.NBTList;
import me.dpohvar.powernbt.api.NBTManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utils, 2/25/2016 1:46 AM
 *
 * @author jb_aero
 */
public class Utils {

	public static CArray compound(NBTCompound compound, Target t) {
		CArray ret = CArray.GetAssociativeArray(t);

		for (Map.Entry<String, Object> entry : compound.entrySet()) {
			ret.set(entry.getKey(), identify(entry.getValue(), t), t);
		}

		return ret;
	}

	public static Construct identify(Object obj, Target t) {
		if (obj instanceof NBTCompound) {
			return compound((NBTCompound) obj, t);
		} else if (obj instanceof NBTList) {
			return list((NBTList) obj, t);
		} else if (obj instanceof Byte || obj instanceof Short || obj instanceof Integer || obj instanceof Long) {
			return new CInt(obj.toString(), t);
		} else if (obj instanceof Float || obj instanceof Double) {
			return new CDouble(obj.toString(), t);
		} else if (obj instanceof byte[]) {
			return (new CByteArray(t)).wrap((byte[]) obj, t);
		} else if (obj instanceof int[]) {
			CArray sub = new CArray(t);
			for (int i : (int[]) obj) {
				sub.push(new CInt(i, t), t);
			}
			return sub;
		} else if (obj instanceof String) {
			return new CString((String) obj, t);
		} else {
			return new CString(obj.getClass().getSimpleName(), t);
		}
	}

	public static CArray list(NBTList list, Target t) {
		CArray ret = new CArray(t);

		switch (list.getType()) {
			case 0:
				break;
			case 11: // int arrays
				for (Object obj : list.toArrayList()) {
					CArray sub = new CArray(t);
					for (int i : (int[]) obj) {
						sub.push(new CInt(i, t), t);
					}
					ret.push(sub, t);
				}
				break;
			case 10: // compounds
				for (Object obj : list.toArrayList()) {
					NBTCompound toWrite;
					if (obj instanceof HashMap) {
						toWrite = new NBTCompound();
						toWrite.merge((Map) obj);
					} else {
						toWrite = (NBTCompound) obj;
					}
					ret.push(compound(toWrite, t), t);
				}
				break;
			case 9: // lists
				for (Object obj : list.toArrayList()) {
					ret.push(list((NBTList) obj, t), t);
				}
				break;
			case 7: // byte array
				for (Object obj : list.toArrayList()) {
					ret.push((new CByteArray(t)).wrap((byte[]) obj, t), t);
				}
				break;
			default:
				Params type = listType(list.getType(), t, list.get(0));
				for (Object obj : list.toArrayList()) {
					try {
						ret.push((Construct) type.type.getConstructor(type.argument, Target.class).newInstance(obj, t), t);
					} catch (InstantiationException | IllegalAccessException
							| InvocationTargetException | NoSuchMethodException e) {
						throw new CREPluginInternalException(e.getMessage(), t, e);
					}
				}
		}

		return ret;
	}

	public static Params listType(byte type, Target t, Object o) {
		try {
			return listType(type, t);
		} catch (CREPluginInternalException e) {
			throw new CREPluginInternalException(e.getMessage() + ", class: " + o.getClass().getSimpleName(), t);
		}
	}

	public static Params listType(byte type, Target t) {
		switch (type) {
			case 1:
			case 2:
			case 3:
			case 4:
				return new Params(CInt.class, long.class);
			case 5:
			case 6:
				return new Params(CDouble.class, double.class);
			case 8:
				return new Params(CString.class, String.class);
			default:
				throw new CREPluginInternalException("Unexpected data tag: " + type, t);
		}
	}

	private static class Params {
		Class<? extends Construct> type;
		Class argument;

		Params(Class<? extends Construct> type, Class argument) {
			this.type = type;
			this.argument = argument;
		}
	}

	public static CArray readBlock(MCBlock block, Target t) {
		return compound(NBTManager.getInstance().read((Block) block.getHandle()), t);
	}

	public static CArray readBlock(MCLocation loc, Target t) {
		return readBlock(loc.getBlock(), t);
	}

	public static void readChunk(MCChunk chunk) {

	}

	public static void readChunk(MCLocation loc) {
		readChunk(loc.getChunk());
	}

	public static CArray readEntity(MCEntity ent, Target t) {
		return compound(NBTManager.getInstance().read((Entity) ent.getHandle()), t);
	}

	public static CArray readUser(MCOfflinePlayer player, Target t) {
		return compound(NBTManager.getInstance().readOfflinePlayer((OfflinePlayer) player.getHandle()), t);
	}

	public static CArray readItem(MCItemStack item, Target t) {
		return compound(NBTManager.getInstance().read((ItemStack) item.getHandle()), t);
	}

	public static CArray readFile(File file, Target t) {
		try {
			return compound(NBTManager.getInstance().readCompressed(file), t);
		} catch (FileNotFoundException e) {
			throw new CREIOException("Could not read file " + file.getAbsolutePath(), t);
		} catch (Exception e2) {
			throw new CREPluginInternalException(e2.getMessage(), t);
		}
	}
}
