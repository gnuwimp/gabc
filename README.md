# gABC
gABC is an open source mp3 audio book converter written in [kotlin](https://kotlinlang.org).<br />
It is an frontend for lame encoder/decoder.<br />
gABC takes a directory or an CD of mp3 files as input and converts them into one single mp3 file.<br />
The bitrate can be changed during transcoding.<br />
32 - 48 kbps is usually sufficient for speech in mono.<br />
All mp3 files must have same samplerate and number of channels (stereo/mono).<br />
The result file will be in mono<br />
Be aware that the files will be transcoded and sound quality will be degraded somewhat.<br />

You will need [Java](http://java.com) installed to run this program.<br />
The actual transcoding of audio files are done by [lame](http://lame.sourceforge.net).<br />
And lame must be in application path.<br />

<hr>

## License & Download
gABC is released under the [GNU General Public License v3.0](LICENSE).<br />
A compiled jar file ready to run can be downloaded [here](bin/gabc.jar)<br />
Double click to start the program or run it from the command line with "<code>java -jar gabc.jar</code>".<br />
Lame for macOS can be installed by using [brew](http://brew.sh).<br />
On Ubuntu use this command "<code>sudo apt-get install lame</code>".<br />
Download a windows 64 bit version [here](bin/lame_win64.zip).<br />

<hr>

## Screenshots
<img src="bin/gabc.png" width="50%" height="50%"/>
