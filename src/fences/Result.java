package fences;

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

	public APIStatus status() {
		return status;
	}

	public E value() {
		return value;
	}
}