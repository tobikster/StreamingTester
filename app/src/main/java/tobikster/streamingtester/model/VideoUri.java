package tobikster.streamingtester.model;

/**
 * Created by tobikster on 2015-05-19.
 */
public class VideoUri
{
	String mName;
	String mUri;
	boolean mRemote;

	public VideoUri(String name, String uri, boolean remote) {
		mName = name;
		mUri = uri;
		mRemote = remote;
	}

	public VideoUri(String name, String uri) {
		this(name, uri, true);
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getUri() {
		return mUri;
	}

	public void setUri(String uri) {
		mUri = uri;
	}

	public boolean isRemote() {
		return mRemote;
	}

	public void setRemote(boolean remote) {
		mRemote = remote;
	}
}