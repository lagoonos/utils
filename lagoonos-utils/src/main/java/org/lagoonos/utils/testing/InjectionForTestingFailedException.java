package org.lagoonos.utils.testing;

/**
 * Exception class for errors occurring during cdi injection for unit testing.
 * <br/>
 * <p>
 * Copyright (C) 2014 Sebastian Gerstenlauer ( gerstenlauer@gmx.net )
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * </p>
 * 
 * @author Sebastian Gerstenlauer ( gerstenlauer@gmx.net )
 */
public class InjectionForTestingFailedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs with the given throwable
	 * @param t the throwable to throw
	 */
	public InjectionForTestingFailedException(Throwable t) {
		super(t);
	}

	/**
	 * Constructs with the given message
	 * @param message the message of the exception
	 */
	public InjectionForTestingFailedException(String message) {
		super(message);
	}

	/**
	 * Constructs with the given message and the original throwable cause
	 * @param message the message of the exception
	 * @param t the original throwable
	 */
	public InjectionForTestingFailedException(String message, Throwable t) {
		super(message, t);
	}
}