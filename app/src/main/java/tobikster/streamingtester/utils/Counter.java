package tobikster.streamingtester.utils;

public
class Counter {
	String mName;
	int mCounter;

	public
	Counter(String name) {
		mName = name;
		reset();
	}

	public
	void add(int count) {
		mCounter += count;
	}

	public
	void reset() {
		mCounter = 0;
	}

	public
	int getCount() {
		return mCounter;
	}

	@Override
	public
	String toString() {
		return String.format("%s: %d", mName, getCount());
	}
}
