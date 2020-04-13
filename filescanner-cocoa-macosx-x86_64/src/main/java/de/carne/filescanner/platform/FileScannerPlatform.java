/*
 * Copyright (c) 2007-2020 Holger de Carne and contributors, All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.carne.filescanner.platform;

import org.eclipse.swt.internal.cocoa.NSBundle;
import org.eclipse.swt.internal.cocoa.NSDictionary;
import org.eclipse.swt.internal.cocoa.NSMutableDictionary;
import org.eclipse.swt.internal.cocoa.NSNumber;
import org.eclipse.swt.internal.cocoa.NSString;
import org.eclipse.swt.internal.cocoa.OS;

import de.carne.boot.logging.Log;

/**
 * Utility class providing platform dependent functions.
 */
public final class FileScannerPlatform {

	private static final Log LOG = new Log();

	FileScannerPlatform() {
		// Prevent instantiation
	}

	/**
	 * Performs platform specific setup tasks.
	 */
	public static void setupPlatform() {
		NSBundle mainBundle = NSBundle.mainBundle();
		boolean atsDefined = false;

		if (mainBundle != null) {
			NSDictionary dictionary = mainBundle.infoDictionary();

			if (dictionary != null
					&& dictionary.isKindOfClass(OS.objc_lookUpClass(NSMutableDictionary.class.getSimpleName()))) {
				NSMutableDictionary mutableDictionary = new NSMutableDictionary(dictionary);
				NSMutableDictionary ats = NSMutableDictionary.dictionaryWithCapacity(3);
				NSNumber trueValue = NSNumber.numberWithBool(true);

				ats.setValue(trueValue, NSString.stringWith("NSAllowsArbitraryLoads"));
				ats.setValue(trueValue, NSString.stringWith("NSAllowsArbitraryLoadsForMedia"));
				ats.setValue(trueValue, NSString.stringWith("NSAllowsArbitraryLoadsInWebContent"));
				mutableDictionary.setObject(ats, NSString.stringWith("NSAppTransportSecurity"));
				atsDefined = true;
			}
		}
		if (!atsDefined) {
			LOG.warning("Failed to setup App Transport Security policy");
		}
	}

	/**
	 * Gets the CSS font size based upon the platform specific font height.
	 *
	 * @param height the font height to get the CSS font size for.
	 * @return the CSS font size.
	 */
	public static float cssFontSize(int height) {
		return height * 0.75f;
	}

}
