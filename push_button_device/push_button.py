from gpiozero import Button
from signal import pause
from send_text import send_txt_msg

push_button = Button(26)

push_button.when_pressed = send_txt_msg

pause()

