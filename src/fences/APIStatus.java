package fences;

/**
 * An <code>APIStatus</code> represents the status of an API request,
 * meaning <code>ok</code> if it was successful or other values for specific errors or problems that can occur.
 * @author Jakob Danckwerts
 */
public enum APIStatus {
	ok, invalid, forbidden, notFound, serverError, error
}