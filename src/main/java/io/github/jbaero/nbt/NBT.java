package io.github.jbaero.nbt;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.CRE.CREThrowable;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;

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
	public static class read extends NBTFunction {

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[] {};
		}

		@Override
		public Construct exec(Target t, Environment env, Construct... args) throws ConfigRuntimeException {
			return null;
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{1};
		}

		@Override
		public String docs() {
			return "array {}";
		}

		@Override
		public Version since() {
			return new SimpleVersion(0,1,0);
		}
	}
}
