import os

def split(s):
	loc = s.find(':')
	if loc > 0:
		s0 = s[0:loc]
		s1 = s[loc+1:].lstrip()
		return (s0, s1)
	return None

class Data:
	def __init__(self, name):
		self.name = name
		self.FuncList = []
		self.LangMap = {}
	
	def ParseLang(self, func, line):
		t = split(line)
		if t == None:
			print 'ERROR: ' + line
			return
		langs = t[0].split(',')
		macro = t[1].replace('"', '\\"').replace("\\", "\\\\")
		for lang in langs:
			if not self.LangMap.has_key(lang):
				self.LangMap[lang] = {}
			self.LangMap[lang][func] = macro
			print lang, func, macro

	def Write(self, lang):
		f = open('lib/'+lang+'/'+self.name+'.bun', 'w')
		for func in self.FuncList:
			t = split(func)
			macro = ""
			if self.LangMap[lang].has_key(func):
				macro = self.LangMap[lang][func]
			line = 'define %s "%s": %s\n' % (t[0].replace('-', '::'), macro, t[1])
			f.write(line)
		f.close()
		
	def GenLib(self):
		for lang in self.LangMap.keys():
			if lang == "#" : continue
			self.Write(lang)			

def Load(name):
	d = Data(name)
	f = open("lib/" + name + ".libbun")
	func = ""
	for line in f:
		line = line[:-1] ## remove \n
		#print line
		if line.startswith("#") or len(line) == 0:
			continue
		if line.startswith(" "):
			d.ParseLang(func, line.lstrip())
		else:
			t = split(line)
			func = t[0]+':'+t[1].lstrip()
			d.FuncList.append(func)

	f.close()
	return d

Load("common").GenLib()
Load("math").GenLib()


