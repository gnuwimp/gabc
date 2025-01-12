# AudioConverter

## About
AudioConverter converts audio and video files of different formats.  
It uses lame/oggenc/ffmpeg to encode and decode the files.  
AudioConverter is  written in [kotlin](https://kotlinlang.org) and released under the [GNU General Public License v3.0](LICENSE).  

## Convert to a single file
Convert a directory of files (typical an audio book) to a single file.  
All input files must have the same samplerate and number of channels (stereo/mono) and bitwidth.  

## Convert file by file
It will convert all files in a directory tree.  
And it will also try to read and copy tags from the source files.  

## Download
Download AudioConverter from [here](https://github.com/gnuwimp/AudioConverter/releases).  
Install [Java](https://java.com).  
Install [ffmpeg](https://www.ffmpeg.org) to decode flac/wav/ogg/m4a/avi/mp4/mkv files.  
Install [Lame](https://lame.sourceforge.io) to encode mp3 files.  
Install [oggenc](https://www.xiph.org/ogg) to encode ogg files.  
Install [qaac64](https://github.com/nu774/qaac/releases) to encode aac files (Windows + iTunes are needed).  

## Usage
Double-click AudioConverter.jar file on windows to start the program.  
Or run it from the command line with <code>java -jar AudioConverter.jar</code>.  
AudioConverter has been tested on Windows 10 and Ubuntu 24.10.  

## Screenshots
<img src="images/audioconverter.png"/><br>
<img src="images/audioconverter-2.png"/>

## Command Line Arguments  
To convert files to a single file use these arguments, (use only ascii characters on Windows).  
And use "" around text and paths with spaces.  
<pre>
--src  [PATH]              source directory with audio files
--dest [PATH]              destination directory for target file
--artist [TEXT]            artist name
--title [TEXT]             album and title name
--comment [TEXT]           comment string (optional)
--cover [PATH]             track cover image (optional)
--year [YYYY]              track year (optional, 1 - 9999)
--genre [TEXT]             genre string (optional, default Audiobook)
--gap [SECONDS]            insert silence between tracks (optional, default 0)
                             valid values are: 0 - 5
--mono                     downmix stereo to mono (optional)
--overwrite [VALUE]        overwrite destination files (optional, default 0)
                             valid values are: 0 dont overwrite, 1 overwrite older, 3 overwrite all
--encoder [INDEX]          index in encoder list (optional, default 4 -> MP3 CBR 128 Kbps)
                             0 = MP3 CBR 32 Kbps
                             1 = MP3 CBR 48 Kbps
                             2 = MP3 CBR 64 Kbps
                             3 = MP3 CBR 96 Kbps
                             4 = MP3 CBR 128 Kbps
                             5 = MP3 CBR 192 Kbps
                             6 = MP3 CBR 256 Kbps
                             7 = MP3 CBR 320 Kbps
                             8 = MP3 VBR ~160 Kbps
                             9 = MP3 VBR ~190 Kbps
                             10 = MP3 VBR ~240 Kbps
                             11 = Ogg ~45 Kbps
                             12 = Ogg ~64 Kbps
                             13 = Ogg ~96 Kbps
                             14 = Ogg ~128 Kbps
                             15 = Ogg ~192 Kbps
                             16 = Ogg ~256 Kbps
                             17 = Ogg ~320 Kbps
                             18 = Ogg ~500 Kbps
                             19 = AAC HE/CBR 48 Kbps
                             20 = AAC HE/CBR 80 Kbps
                             21 = AAC CVBR ~96 Kbps
                             22 = AAC TVBR63 ~128 Kbps
                             23 = AAC TVBR109 ~256 Kbps
                             24 = AAC TVBR127 ~320 Kbps
                             25 = AAC ALAC
--auto                     start automatically and quit after successful encoding (optional)
--auto2                    start automatically and quit even for error (optional)
</pre>

To convert file to file use these arguments:
<pre>
--mode2                    set this mode
--src [PATH]               start directory with audio files
--dest [PATH]              destination directory for target file
--threads [COUNT]          set number of threads to use (optional, default 1)
                             valid values are: 1, 2, 3, 4, 5, 6, 7, 8, 12, 16, 24, 32, 48, 64, 96, 128
--overwrite [VALUE]        overwrite destination files (optional, default 0)
                             valid values are: 0 dont overwrite, 1 overwrite older, 3 overwrite all
--encoder [INDEX]          same options as above
--auto                     start automatically and quit after successful encoding (optional)
--auto2                    start automatically and quit even for error (optional)
</pre>

## Changes
<pre>
2.6:    added overwrite destination files option
        some bug fixes and improvements

2.5:    added aac encoding (Windows only, requires iTunes + qaac64)
        minor improvements
        updated Jaudiotagger library and added it to the source tree

2.4:    renamed again from toMP3 to AudioConverter
        added support for file to file conversion
        ui changes
        bug fixes

2.3:    added jaudiotagger for tag writing
        added support for ogg encoding
        ui changes
        bitrate selection changed

2.2:    renamed from gabc to toMP3
        added support for more input files (m4a/flac/ogg/wav/avi/mkv/mp4)
        options for mono/stereo and vbr
        insert silence between tracks
        bug fixes

2.1:    bug fixes
</pre>
