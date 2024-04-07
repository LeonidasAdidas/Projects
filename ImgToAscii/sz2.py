from socket import *
import threading
import os.path
import re
import subprocess

def postRequest(line):
    if re.search(r"^POST", line):
        return True
    else:
        return False

def extension(byteArray):
    extension = byteArray[(byteArray.index(b'Content-Type:') + len('Content-Type:')):]
    extension = extension[:extension.index(b'\r\n\r\n')]
    if re.search(b'jpeg' ,extension) or re.search(b'jpg' ,extension):
        return '.jpg'
    elif re.search(b'png' ,extension):
        return '.png'
    
def saveFile(byteArray, connectionSocket):
    startInd = byteArray.index(b'\r\n\r\n')
    ext = extension(byteArray)
    filename = str(threading.current_thread().ident)
    filename = filename + ext
    try:
        endInd = byteArray.index(b'\r\n--')
    except:
        endInd = len(byteArray)
    finally:
        file = open(filename, 'wb')
    file.write(byteArray[(startInd + 4):endInd])
    try:
        byteArray = connectionSocket.recv(65535)
        while len(byteArray) != 0:
            file.write(byteArray)
            byteArray = connectionSocket.recv(65535)
    except Exception as e:
        pass
    return filename

def clientThread(connectionSocket, addr):
    connectionSocket.settimeout(5)
    try:
        while True:
            incomingMessage = connectionSocket.recv(2048)
            if postRequest(incomingMessage[:10].decode()):
                nextMessage = connectionSocket.recv(65535)
                filename = saveFile(nextMessage, connectionSocket)
                imgascii = subprocess.run(["E:\\Lecke\\Whoop\\Fun\\not_fun\\ascii_art.exe", filename], capture_output = True, text = True)
                os.remove(filename)
                ansfile = open(str(addr[0]).replace('.', '_') + ".html", 'w')
                if imgascii.returncode == 0:
                    ansfile.write(open('baseanswer.html', 'r').read() + imgascii.stdout.replace("\n", "<br>") + '</tt></p></body></html>')
                    ansfile.close()
                else:
                   ansfile.write(open('baseanswer.html', 'r').read() + 'Some error occured' + '</tt></p></body></html>')
                   print("Some error occured:" + imgascii.stderr)
                red_response = "HTTP/1.1 303 See Other\r\nLocation: /answer.html\r\n\r\n"
                connectionSocket.send(red_response.encode())
                continue
            incomingMessage = incomingMessage.decode()
            if len(incomingMessage) == 0:
                break
            
            file = incomingMessage.split(" ")[1][1:]
            redirect = False
            if file == "answer.html":
                file = str(addr[0]).replace('.', '_') + ".html"
                redirect = True
            
            finalRequest = False
            conncectionType = "keep-alive"
            for line in incomingMessage.split("\n"):
                if re.search("^Connection:", line):
                    conncectionType = line.split(" ")[1]
                    if conncectionType == "close":
                        finalRequest = True
                break

            filePath = "E:\\Lecke\\Whoop\\Fun\\not_fun\\" + file
            
            if os.path.isfile(filePath):
                returnMessageContent = open(filePath, "rb").read()
                returnMessageHeader = "HTTP/1.1 200 OK\nContent-Type: " + fileTypes[file.split(".")[1]]+ "\nConnection: " + conncectionType + "\nContent-Length: " + str(len(returnMessageContent)) + "\n\n"
                returnMessage = returnMessageHeader.encode() + returnMessageContent
            else:
                returnMessage = "HTTP/1.1 404 File not found\nContent-Type: " + fileTypes[file.split(".")[1]] + "\nConnection: " + conncectionType + "\nContent-Length: 0\n\n"
                returnMessage = returnMessage.encode()
            
            connectionSocket.send(returnMessage)
            if redirect == True:
                os.remove(filePath)
            if finalRequest:
                break
            
    except Exception as e:
        #print(e)
        connectionSocket.close()



serverPort = 11001
serverSocket = socket(AF_INET, SOCK_STREAM)
serverSocket.bind(('', serverPort))
serverSocket.listen()
fileTypes = {'html': 'text/html','css': 'text/css','jpg': 'image/jpeg', 'png': 'image/png', 'mp4': 'video/mp4', 'ico': 'image/png', }

while True:
    connectionSocket, clientAddress = serverSocket.accept()
    
    thread = threading.Thread(target=clientThread, args=(connectionSocket,clientAddress,))
    thread.start()