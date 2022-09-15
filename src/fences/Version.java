package fences;

/**
 * Represents a Fences Server version.<br>
 * A version consists of three elements:
 * <ol>
 * 	<li>The major version number which identifies major points in the program's development.</li>
 * 	<li>The minor version number which identifies minor steps that improved but not fundamentally changed the program.</li>
 * 	<li>The fix number which identifies little things that were done to improve the program, like a small bug fix, changing an error message or similar things.</li>
 * </ol>
 * @author Jakob Danckwerts
 */
public class Version implements Comparable<Version> {
	protected int major;
	protected int minor;
	protected int fix;
	
	public Version(int major, int minor, int fix) {
		this.major = major;
		this.minor = minor;
		this.fix = fix;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getFix() {
		return fix;
	}
	
	@Override
	public int compareTo(Version v) {
		if(major > v.major) return 1;
		if(major < v.major) return -1;
		if(minor > v.minor) return 1;
		if(minor < v.minor) return -1;
		if(fix > v.fix) return 1;
		if(fix < v.fix) return -1;
		return 0;
	}
	
	@Override
	public String toString() {
		return "v" + major + "." + minor + "." + fix;
	}
}