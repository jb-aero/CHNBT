package io.github.jbaero.nbt;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.core.extensions.AbstractExtension;
import com.laytonsmith.core.extensions.MSExtension;

/**
 * CHNBT, 2/25/2016 1:31 AM
 *
 * @author jb_aero
 */
@MSExtension("${project.name}")
public class CHNBT extends AbstractExtension {

	@Override
	public Version getVersion() {
		return new SimpleVersion("${project.version}");
	}

	@Override
	public void onStartup() {
		System.out.println("${project.name} " + getVersion() + " loaded.");
	}

	@Override
	public void onShutdown() {
		System.out.println("${project.name} " + getVersion() + " unloaded.");
	}
}
