
from subprocess import call
from subprocess import STDOUT
from subprocess import DEVNULL
import argparse
import re
import os.path
from os import listdir

HEB_CODESET = "iso8859-8"
HEB_LANG = "heb"
REV_HEB_LANG = "und"
ENG_CODESET = "iso8859-8"
ENG_LANG = "eng"

video_type = "series" # movie or series

NOT_ALLOWED_FILES = ['srt', 'm4v']
ALLOWED_FILES = ['avi', 'mp4', 'mkv']

def ParseInput():
    parser = argparse.ArgumentParser()
    group = parser.add_mutually_exclusive_group()
    group.add_argument("-m", "--movie", action="store_false", help="check if movie")
    group.add_argument("-s", "--series", action="store_false", help="check if series [default]")
    parser.add_argument("input", help="the input file")

    args = parser.parse_args()

    if args.movie:
        videoType = 'movie'
    if args.series:
        videoType = 'series'

    f = args.input
    print(f)
    if f:
        if (os.path.isfile(f)):
            return [f]
        elif (os.path.isdir(f)):
            fileList = GetFileList(f)
            return fileList
        elif (os.path.isdir(f[:-1])):
            fileList = GetFileList(f[:-1])
            return fileList

    return None

def GetFileList(dir):
    list = []
    for f in listdir(dir):
        print(f)
        if os.path.isfile(os.path.join(dir, f)):
            if (re.split('[.]', f)[-1] in ALLOWED_FILES):
                list.append(os.path.join(dir, f))
    return list

def ReversePuctuation(file, newfile):
    f = open(file, 'r')
    nf = open(newfile, 'w')

    #print("both files opened")

    for line in f:
        if len(line)>2:
            nf.write(FixOneLine(line[:-1]) + line[-1])
        else:
            nf.write(line)

    f.close()
    nf.close()

def FixOneLine(s):
    SpecialChars = '.,:;''()-?!+=*&$^%#@~`" /'
    Prefix = ''
    Suffix = ''

    while (len(s) > 0 and s[0] in SpecialChars):
        Prefix += s[0]
        s = s[1:]

    while (len(s) > 0 and s[-1] in SpecialChars):
        Suffix += s[-1]
        s = s[:-1]

    if Prefix == ' -':
        Prefix = '- '
    if Suffix == ' -':
        Suffix = '- '

    return Suffix + s + Prefix

def CheckSubtitles(hebSub, engSub):
    if (os.path.isfile(hebSub) and os.path.isfile(engSub)):
        return 'both'
    if (os.path.isfile(hebSub)):
        return 'heb'
    if (os.path.isfile(engSub)):
        return 'eng'
    return 'None'

escape_dict={'\a':r'\a',
             '\b':r'\b',
             '\c':r'\c',
             '\f':r'\f',
             '\n':r'\n',
             '\r':r'\r',
             '\t':r'\t',
             '\v':r'\v',
             '\'':r'\'',
             '\"':r'\"',
             '\0':r'\0',
             '\1':r'\1',
             '\2':r'\2',
             '\3':r'\3',
             '\4':r'\4',
             '\5':r'\5',
             '\6':r'\6',
             '\7':r'\7',
             '\8':r'\8',
             '\9':r'\9'}

def raw(text):
    """Returns a raw string representation of text"""
    new_string=''
    for char in text:
        try: new_string+=escape_dict[char]
        except KeyError: new_string+=char
    return new_string

def AddAppleMovieData(fileName):
    print('not adding movie data')

def AddAppleSeriesData(fileName):
    # find series name, season, episode
    match = re.search("(.+).S(\d{2})E(\d{2})", re.split(r'[\\]', fileName)[-1])
    if (match and match.lastindex == 3):
        seariesName = match.group(1)
        seariesName = " ".join(seariesName.split('.'))
        season = str(int(match.group(2)))
        episode = str(int(match.group(3)))
    else:
        print("Can't parse file name for series name, season and episode.")
        return

    # build command
    jarName = 'test.jar'
    videoType = 'series'

    command = ['java', '-jar']
    command.append(jarName)
    command.append(videoType)
    command.append(fileName)
    command.append(seariesName)
    command.append(season)
    command.append(episode)

    print("The apple data command:")
    print(command)

    call(command)

def Main():

    hbFullPath = "HandBrakeCLI.exe"

    fileList = ParseInput()
    if (not fileList):
        fileList = "f1.avi"

    print (fileList)

    print("Found " + str(len(fileList)) + " files. Starting to convert")

    for file in fileList:
        print("Converting File: " + re.split(r'[\\]', file)[-1])

        # define file names
        outputFile = ".".join(re.split('[.]', file)[:-1]) + ".m4v"
        hebSubtitleFile = ".".join(re.split('[.]', file)[:-1]) + ".srt"
        if (not os.path.isfile(hebSubtitleFile)):
            hebSubtitleFile = ".".join(re.split('[.]', file)[:-1]) + ".HEB.srt"
        revHebSubtitleFile = ".".join(re.split('[.]', file)[:-1]) + ".HEB.REV.srt"
        engSubtitleFile = ".".join(re.split('[.]', file)[:-1]) + ".EN.srt"
        if (not os.path.isfile(engSubtitleFile)):
            engSubtitleFile = ".".join(re.split('[.]', file)[:-1]) + ".ENG.srt"

        # check for existing subtitles
        subStatus = CheckSubtitles(hebSubtitleFile, engSubtitleFile)
        print('Subtitle available: ' + subStatus)

        # if exist hebrew subtitle, add reversed punctuation
        if (subStatus == 'both' or subStatus == 'heb'):
            ReversePuctuation(hebSubtitleFile, revHebSubtitleFile)

        # create command
        hbFullOptions = ""
        hbFullOptions2 = [hbFullPath]
        # input
        hbFullOptions += "--input \"" + file + "\""
        hbFullOptions2.append('--input')
        hbFullOptions2.append(file)
        # output
        hbFullOptions += " --output \"" + outputFile + "\""
        hbFullOptions2.append('--output')
        hbFullOptions2.append(outputFile)
        # subtitle
        if (subStatus == 'both'):
            hbFullOptions2.append('--srt-file')
            hbFullOptions2.append(hebSubtitleFile + "," + revHebSubtitleFile + "," + engSubtitleFile)
            hbFullOptions2.append('--srt-codeset')
            hbFullOptions2.append(HEB_CODESET + "," + HEB_CODESET + "," + ENG_CODESET)
            hbFullOptions2.append('--srt-lang')
            hbFullOptions2.append(HEB_LANG + "," + REV_HEB_LANG + "," + ENG_LANG)
            hbFullOptions2.append('--srt-default')
        elif (subStatus == 'heb'):
            hbFullOptions2.append('--srt-file')
            hbFullOptions2.append(hebSubtitleFile + "," + revHebSubtitleFile)
            hbFullOptions2.append('--srt-codeset')
            hbFullOptions2.append(HEB_CODESET + "," + HEB_CODESET)
            hbFullOptions2.append('--srt-lang')
            hbFullOptions2.append(HEB_LANG + "," + REV_HEB_LANG)
            hbFullOptions2.append('--srt-default')
        elif (subStatus == 'eng'):
            hbFullOptions2.append('--srt-file')
            hbFullOptions2.append(engSubtitleFile)
            hbFullOptions2.append('--srt-codeset')
            hbFullOptions2.append(ENG_CODESET)
            hbFullOptions2.append('--srt-lang')
            hbFullOptions2.append(ENG_LANG)
            hbFullOptions2.append('--srt-default')
        else:
            print ("no Hebrew or English subtitle file, converting video without sub")

        #print ("The command to run:")
        #print (hbFullCall)
        if (not os.path.isfile(outputFile)):
            #print ("Starting to encode file: " + file)
            call(hbFullOptions2, stderr=DEVNULL)
        else:
            print(outputFile + " already exists, jumping conversion")

        if (video_type == 'series'):
            AddAppleSeriesData(outputFile)
        else:
            AddAppleMovieData(outputFile)

    print ('total files: ' + str(len(fileList)))

if __name__ == '__main__':
    Main()
