                     ___..-.---.---.--..___
               _..-- `.`.   `.  `.  `.      --.._
              /    ___________\   \   \______    \
              |   |.-----------`.  `.  `.---.|   |
              |`. |'  \`.        \   \   \  '|   |
              |`. |'   \ `-._     `.  `.  `.'|   |
             /|   |'    `-._o)\  /(o\   \   \|   |\
           .' |   |'  `.     .'  '.  `.  `.  `.  | `.
          /  .|   |'    `.  (_.==._)   \   \   \ |.  \         _.--.
        .' .' |   |'      _.-======-._  `.  `.  `. `. `.    _.-_.-'\\
       /  /   |   |'    .'   |_||_|   `.  \   \   \  \  \ .'_.'     ||
      / .'    |`. |'   /_.-'========`-._\  `.  `-._`._`. \(.__      :|
     ( '      |`. |'.______________________.'\      _.) ` )`-._`-._/ /
      \\      |   '.------------------------.'`-._-'    //     `-._.'
      _\\_    \    | AMIGA  O O O O * * `.`.|    '     //
     (_  _)    '-._|________________________|_.-'|   _//_
     /  /      /`-._      |`-._     / /      /   |  (_  _)
   .'   \     |`-._ `-._   `-._`-._/ /      /    |    \  \
  /      `.   |    `-._ `-._   `-._|/      /     |    /   `.
 /  / / /. )  |  `-._  `-._ `-._          /     /   .'      \
| | | \ \|/   |  `-._`-._  `-._ `-._     /     /.  ( .\ \ \  \
 \ \ \ \/     |  `-._`-._`-._  `-._ `-._/     /  \  \|/ / | | |
  `.\_\/       `-._  `-._`-._`-._  `-._/|    /|   \   \/ / / /
              /    `-._  `-._`-._`-._  ||   / |    \   \/_/.'
            .'         `-._  `-._`-._  ||  /  |     \
   LGB     /           / . `-._  `-._  || /   |      \
          '\          / /      `-._    ||/'._.'       \
           \`.      .' /           `-._|/              \
            `.`-._.' .'               \               .'
              `-.__\/                 `\            .' '
                                       \`.       _.' .'
                                        `.`-._.-' _.'
                                          `-.__.-'

ReadMe!!

This is Lab 2 in Computer Networks class IDC 2011
Johnathan Levison 039869193 and Assaf Mashiah 061110185




Base64Coder
Used for base64 encoding - we did not write this, its from the internet.

ChatBuddyData
The chat uses this to keep the history of the chat messages

ChatService
Handles all chat_service commands

ChatServiceProxy
Proxy for the new_message command

CommandsService
From lab1, added the get_main_page to it

CommandsServiceProxy
To test the Proxy of echo -> it was not required as part of the lab (but not really a bonus)

ConfigException
Raised when a config parsing error happens

ConfigParser
Our configuration parser, did not change from lab1

CSSService
Handles CSS external files (more in bonus :) )
Eventually we got to a point where it was not needed - the ImageService (another bonus) handles it.

DispatcherServer
Starts the dispatcher server, handles initialization of the application

DispatcherThread
The main processing thread, will process the request incoming on the server, call the service and generate the response.
There are not many changes here from lab 1 aside for adding support to all the new services.

FilesService
Handles the files_serivce requests
The 'download_file_from' command is new, and handles the proxy call of actually downloading a file
The 'download_file' performs the reading the encoding the file

FilesServiceProxy
Handles the remote download process and saving on the disk
Also have a 'get_file_list' to get remote file lists

FriendInfo
The data structure for a single friend we keep

FriendRequestServer
The UDP server for 'Be my friend?' broadcast

FriendRequestThread
The thread to handle the 'Be my friend?' -> it starts the friendship protocol

FriendService
Manages all friend related info

FriendServiceProxy
Manages the RPC for the friend protocol

HTMLTamplate
The HTML template engine (very basic - sort of a bonus, this extend the application)
Enables inplace replacements inside a template

HttpHeader
HttpHeaderParserException
HttpNtImplementedException
   From lab 1, our header object and two parsing exceptions


HttpParser
The main Http parser, got some changes from lab1 to support more features

HttpProxyException
HttpQueryStringType
HttpRequestMethod
   From lab1, another exception and helper objects for the request handling

HttpRequestParser
The request parsing objects, minor changes from lab1 to help us support the proxy better

HttpRequestParserException
From lab1... happens when there is a generic parsing error

HttpResponseCode
Helps us generate responses, added 301 and 408 which were not required

HttpResponseParser
The main response parser object, generates a response
Added functionallity from lab1

HttpResponseParsingException
From lab1, happens where there is a generic response generation problem.

HttpServiceException
Happens when there is a problem in the services (=500 response)

HttpServices
The servies definition (what services are legit, this enables us to add services in a real simple way)

HttpURIData
Should be a struct (no structs in java), it just stored the function name and the service name

HttpVersion
Stores the HTTP version of the current request/response
Supporting 1.0 and 1.1

IChatService
ICommandsService
IFileService
IFriendService
     Defines all the known functions for the services

ImageService
Bonus! :) adds images support!

Lab2Utils
Generic utils for getting time and other styling related issues
Also reads and writes files

ProxyBase
The abstract base class for the proxy

Starter
The main().
Just starts the program and the two servers

Tracer
TracerFileWriter
Bonus! tracing and log file writing features