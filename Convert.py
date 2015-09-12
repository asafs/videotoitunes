from subprocess import call
import argparse
import re
import os.path
from os import listdir

HEB_CODESET = "iso8859-8"
HEB_LANG = "heb"
REV_HEB_LANG = "und"
ENG_CODESET = "iso8859-8"
ENG_LANG = "eng"

TYPE_MOVIE = 'movie'
TYPE_SERIES = 'series'
video_type = TYPE_SERIES  # movie or series

NOT_ALLOWED_FILES = ['srt', 'm4v']
ALLOWED_FILES = ['avi', 'mp4', 'mkv']
FIX_APPLE_MP4_FILE_PATH = 'dist/test.jar'
HAND_BRAKE_PATH = 'dist/HandBrakeCLI.exe'
OUTPUT_NULL = open(os.devnull, 'w')


def parse_input():
    """
    Parse the input arguments to the script.

    :return: A list of file names to fix.
    """
    parser = argparse.ArgumentParser()
    group = parser.add_mutually_exclusive_group()
    group.add_argument("-m", "--movie", action="store_true", help="check if movie")
    group.add_argument("-s", "--series", action="store_true", help="check if series [default]")
    parser.add_argument("input", help="the input file")

    args = parser.parse_args()

    global video_type
    if args.movie:
        video_type = TYPE_MOVIE
    if args.series:
        video_type = TYPE_SERIES

    f = args.input
    if f:
        if os.path.isfile(f):
            return [f]
        elif os.path.isdir(f):
            return get_file_list(f)
        elif os.path.isdir(f[:-1]):
            return get_file_list(f[:-1])

    # should not get here, throw error
    raise RuntimeError('No valid video files found in directory. Exiting.')


def get_file_list(directory_path):
    """
    Gets the path of the directory with all the files.

    :param directory_path: path with the files
    :return: a list of all the files.
    """
    file_list = []
    for f in listdir(directory_path):
        if os.path.isfile(os.path.join(directory_path, f)):
            if re.split('[.]', f)[-1] in ALLOWED_FILES:
                file_list.append(os.path.join(directory_path, f))
    return file_list


def reverse_punctuation(old_file_name, new_file_name):
    """
    Creates a new file of the reversed punctuation.

    :param old_file_name
    :param new_file_name
    """
    f = open(old_file_name, 'r')
    nf = open(new_file_name, 'w')

    #print("both files opened")

    for line in f:
        if len(line) > 2:
            nf.write(fix_one_line(line[:-1]) + line[-1])
        else:
            nf.write(line)

    f.close()
    nf.close()


def fix_one_line(s):
    """
    Fixes punctuation in one line.

    :param s: the string to fix
    :return: the fixed string
    """
    special_characters = '.,:;''()-?!+=*&$^%#@~`" /'
    prefix = ''
    suffix = ''

    # get special chars from the beginning of the string
    while len(s) > 0 and s[0] in special_characters:
        prefix += s[0]
        s = s[1:]

    # get special chars at the end of the string
    while len(s) > 0 and s[-1] in special_characters:
        suffix += s[-1]
        s = s[:-1]

    if prefix == ' -':
        prefix = '- '
    if suffix == ' -':
        suffix = '- '

    return suffix + s + prefix


def check_subtitles(video_file_name):
    """
    Check if both subs exist.

    :param heb_sub: path to heb sub.
    :param eng_sub: path to eng sub.
    :return: string that describes which subs exist.
    """
    # if os.path.isfile(heb_sub) and os.path.isfile(eng_sub):
    #     return 'both'
    # if os.path.isfile(heb_sub):
    #     return 'heb'
    # if os.path.isfile(eng_sub):
    #     return 'eng'
    # return None

    heb_sub_file = ".".join(re.split('[.]', video_file_name)[:-1]) + ".HEB.srt"
    eng_sub_file = ".".join(re.split('[.]', video_file_name)[:-1]) + ".ENG.srt"
    # if no subtitle with HEB, try without it
    if not os.path.isfile(heb_sub_file):
        heb_sub_file = ".".join(re.split('[.]', video_file_name)[:-1]) + ".srt"

    print("Output file is: {0}, hebrew sub file is {1}, english sub file is {2}"
          .format(video_file_name, heb_sub_file, eng_sub_file))

    # check existence of subtitle files and get correct hand brake params
    srt_file = None
    srt_codeset = None
    srt_lang = None
    if os.path.isfile(heb_sub_file) and os.path.isfile(eng_sub_file):
        srt_file = heb_sub_file + "," + eng_sub_file
        srt_codeset = HEB_CODESET + "," + ENG_CODESET
        srt_lang = HEB_LANG + "," + ENG_LANG
    elif os.path.isfile(heb_sub_file):
        srt_file = heb_sub_file
        srt_codeset = HEB_CODESET
        srt_lang = HEB_LANG
    elif os.path.isfile(eng_sub_file):
        srt_file = eng_sub_file
        srt_codeset = ENG_CODESET
        srt_lang = ENG_LANG

    return srt_file, srt_codeset, srt_lang


def raw(text):
    """
    Returns a raw string representation of text.

    :param text: the input text
    :return: the raw representation.
    """

    escape_dict = {'\a': r'\a',
                   '\b': r'\b',
                   '\c': r'\c',
                   '\f': r'\f',
                   '\n': r'\n',
                   '\r': r'\r',
                   '\t': r'\t',
                   '\v': r'\v',
                   '\'': r'\'',
                   '\"': r'\"',
                   '\0': r'\0',
                   '\1': r'\1',
                   '\2': r'\2',
                   '\3': r'\3',
                   '\4': r'\4',
                   '\5': r'\5',
                   '\6': r'\6',
                   '\7': r'\7',
                   '\8': r'\8',
                   '\9': r'\9'}

    new_string = ''
    for char in text:
        try:
            new_string += escape_dict[char]
        except KeyError:
            new_string += char
    return new_string


def add_apple_movie_data(file_name):
    print('not adding movie data for file ' + file_name)


def add_apple_series_data(file_name):
    """
    Parse the file name for series name, season and episode and add it to the mp4 file.
    Supports only SXXEXX format for now.

    :param file_name
    """

    # find series name, season, episode
    match = re.search("(.+).S(\d{2})E(\d{2})", re.split(r'[\\]', file_name)[-1])
    if not match:
        match = re.search("(.+).s(\d{2})e(\d{2})", re.split(r'[\\]', file_name)[-1])
    if match and match.lastindex == 3:
        series_name = match.group(1)
        series_name = " ".join(series_name.split('.'))
        season = str(int(match.group(2)))
        episode = str(int(match.group(3)))
    else:
        print("Can't parse file name for series name, season and episode.")
        return

    # build command
    command = ['java', '-jar', FIX_APPLE_MP4_FILE_PATH, video_type, file_name, series_name, season, episode]

    print("The apple data command:")
    print(command)

    call(command)


def main_run():
    list_of_files = parse_input()

    print("Found {0} files. Starting to convert".format(len(list_of_files)))

    num_of_converted = 0
    for video_file in list_of_files:
        print("Converting File: " + re.split(r'[\\]', video_file)[-1])

        # set file names
        output_video_file = ".".join(re.split('[.]', video_file)[:-1]) + ".m4v"
        if os.path.isfile(output_video_file):
            print(output_video_file + " already exists, skipping conversion")
            continue

        # check for existing subtitles
        srt_file, srt_codeset, srt_lang = check_subtitles(output_video_file)

        # create command with srt configuration for hand brake
        hand_brake_options = [HAND_BRAKE_PATH, '--input', video_file, '--output', output_video_file]
        # if srt_file:
        #     hand_brake_options += ['--srt-file', srt_file, '--srt-codeset', srt_codeset, '--srt-lang', srt_lang,
        #                            '--srt-default']
        # else:
        #     print ("no Hebrew or English subtitle file, converting video without sub")

        # print ("The command to run: " + hand_brake_options)
        call(hand_brake_options, stderr=OUTPUT_NULL)

        # add the other data
        global video_type
        if video_type == TYPE_SERIES:
            add_apple_series_data(output_video_file)
        elif video_type == TYPE_MOVIE:
            add_apple_movie_data(output_video_file)

        num_of_converted += 1

    print ('total files converted: {0}'.format(num_of_converted))


if __name__ == '__main__':
    main_run()
