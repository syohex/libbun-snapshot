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

## @SoftwareFault;@Fault
class SoftwareFault(Fault):
	def __init__(self, msg):
		self.msg = msg

## @mapget;@SoftwareFault
def mapget(m,k):
	if m.has_key(k):
		return m[k]
	raise SoftwareFault('undefined key: ' + k)

## @strnull
def libbun_strnull(s) : 
	return s if s != None else "null"

## @catch
def libbun_catch(e):
	return e


