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

## @strnull
def libbun_strnull(s) : 
	return s if s != None else "null"

## @catch
def libbun_catch(e):
	return e

## @arraysize
def libbun_arraysize(a, n, v):
    while len(a) < n: a.append(v)
    while len(a) > n: a.pop()

## @mapget
def libbun_mapget(m, k, v):
    if m.has_key(k): return m[k]
    else: return v


