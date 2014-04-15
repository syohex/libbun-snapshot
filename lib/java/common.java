// @Fault
class Fault extends RuntimeException {
	class Fault(String msg) {
  		super(msg);
	}
}

// @SoftwareFault;@Fault
class SoftwareFault extends Fault {
	class SoftwareFault(String msg) {
		super(msg);
	}
}

// @catch
class LibCatch {
	public final static Fault f(Object o) {
		return new Fault(o.toString());
	}
}

// @map;SoftwareFault
class LibMap {
	public final <T> Get(HashMap<String,T> m, String key) {
		T o = m.get(key);
		if(o == null) {
			throw new SoftwareFault("key not found: " + key);
		}
		return o;
	}
	public final <T> Get(HashMap<String,T> m, String key, T defval) {
		T o = m.get(key);
		if(o == null) {
			return defval;
		}
		return o;
	}
}

