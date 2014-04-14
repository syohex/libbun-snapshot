## @main
if __name__ == "__main__":
	main()

## @error;@SoftwareFault
def libbun_error(msg):
	raise SoftwareFault(msg)

## @Fault
class Fault:
	def __init__(self, msg):
		self.msg = msg
	def __str__(self):
		return self.msg

## @SoftwareFault;@Fault
class SoftwareFault(Fault):
	def __init__(self, msg):
		self.msg = unicode(msg)

## @null
def libbun_null(s) : 
	return s if s != None else u'null'

## @catch;@SoftwareFault
def libbun_catch(e):
	if isinstance(e, Fault):
		return e
	if isinstance(e, ZeroDivisionError):
		return SoftwareFault(str(e))
	if isinstance(e, KeyError):
		return SoftwareFault('key not found: "%s"' % e)
	if isinstance(e, IndexError):
		return SoftwareFault(str(e))
	return Fault(str(e))

## @arraysize
def libbun_arraysize(a, n, v):
	while len(a) < n: a.append(v)
	while len(a) > n: a.pop()

## @mapget
def libbun_mapget(m, k, v):
	if m.has_key(k): return m[k]
	else: return v


