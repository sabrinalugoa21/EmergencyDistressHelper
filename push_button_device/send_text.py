from twilio.rest import Client

account_sid = "ACd51283a55fad386b1cadeb8e395fffff" # twilio acount SID
auth_token = "" # auth token

client = Client(account_sid, auth_token)

twilio_num = "+19205192974"

contact_file = open("contact.txt", "r")
contact_num = contact_file.readline()
contact_file.close()

message_file = open("message.txt", "r")
user_message = message_file.read()
message_file.close()

txt_msg = user_message + "\n"
original_txt = txt_msg

def get_gps():
    exec(open("get_gps.py").read())
    gps_file = open("gps_data.txt", "r")
    latitude = gps_file.readline()
    longitude = gps_file.readline()
    gps_file.close()

    global txt_msg
    txt_msg = txt_msg + latitude + longitude

def send_txt_msg():
    get_gps()
    global txt_msg
    message = client.api.account.messages.create(
                to = contact_num, # contact number here
                from_ = twilio_num, # twilio number here
                body = txt_msg
                )
    global original_txt
    txt_msg = original_txt
    print("Emergency Text Sent!")

