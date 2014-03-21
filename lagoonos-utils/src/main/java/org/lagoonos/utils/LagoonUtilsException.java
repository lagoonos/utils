package org.lagoonos.utils;

/**
 * 	Exception class for errors occurring on lagoonOS utils operations.
 * <br/>
 * <p>
    Copyright (C) 2014  Sebastian Gerstenlauer ( gerstenlauer@gmx.net )

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
 	any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
    </p>
 * @author Sebastian Gerstenlauer ( gerstenlauer@gmx.net )
 */
public class LagoonUtilsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LagoonUtilsException(String message) {
		super(message);
	}

	public LagoonUtilsException(String message, Throwable cause) {
		super(message, cause);
	}

	public LagoonUtilsException(Throwable cause) {
		super(cause);
	}
	
	public <T> LagoonUtilsException(Throwable cause, ExceptionType type, String ...args) {
		super(createExceptionTypeMessage(type, args), cause);
	}
	
	private static String createExceptionTypeMessage(ExceptionType type, String ...args) {
		switch(type){
			case CLASS_NEW_INSTANCE: return "Class could not instantiate a new instance of class '"+args[0]+"'";
			case INVOKE_METHOD: return "Could not invoke method '"+args[0]+"' on object of type '"+args[1]+"'";
		}
		return null;
	}

	enum ExceptionType{
		CLASS_NEW_INSTANCE, INVOKE_METHOD
	}
}
