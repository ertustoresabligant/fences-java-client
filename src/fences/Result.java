package fences;

/**
 * A <code>Result</code> stores all necessary information about an API request result.<br>
 * Besides a generic return value object, it provides a status which tells whether the operation was successful or, if it wasn't, what went wrong.<br>
 * @author Jakob Danckwerts
 */
public class Result <E> {
	private APIStatus status;
	private E value;
	
	public Result(APIStatus status, E value) {
		this.status = status;
		this.value = value;
	}
	
	public Result(APIStatus status) {
		this.status = status;
		this.value = null;
	}
	
	public Result(E value) {
		this.status = APIStatus.ok;
		this.value = value;
	}
	
	public Result() {
		this.status = APIStatus.ok;
		this.value = null;
	}
	
	@Override
	public String toString() {
		return "Result { status=" + (this.status == null ? "?" : this.status.name()) + ", " + this.value + " }";
	}
	
	/**
	 * Returns the API response status. <code>ok</code> if the operation was successful, any other value is a description of what went wrong.
	 * @author Jakob Danckwerts
	 */
	public APIStatus status() {
		return status;
	}
	
	/**
	 * Returns the value provided by the API.<br>
	 * Please note that in most cases where an error occurs, there is no value.
	 * @author Jakob Danckwerts
	 */
	public E value() {
		return value;
	}
}