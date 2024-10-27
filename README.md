# Mass SMS

Mass SMS is a powerful application that allows you to send SMS messages using SMTP servers. With a feature-rich graphical user interface (GUI) and advanced functionalities, Mass SMS is designed for users who need a robust solution for bulk messaging.

## Features

- **Hot Words**: Send messages with randomly selected words from a provided file to enhance engagement.
- **Personal Words**: Customize your messages with names, addresses, or any other specific information in your leads.
- **Rotate SMTP Servers**: Seamlessly switch between multiple SMTP servers to ensure message delivery.
- **Complete GUI**: A user-friendly graphical interface for easy navigation and configuration.
- **Spoof From**: Ability to spoof the "From" address for more personalized messages.
- **International Support**: Send messages globally with support for various international carriers.

## Known Issues

 - Settings is not displaying saved settings if you press back and go back into settings (Don't wory they are there)
 - Spoofing sender is not working (If you know hot to do this please reach out on telegram @krummy01)
 - Data247 has been being a pain so I have not tested the Test STMP button or the Send button since building gui

## Coming Soon

Stay tuned for upcoming features, including:

- Fix for spoofing "From" address
- Multithreading for improved performance
- Removal of Data247 dependency for proformance and usage costs
- More extensive testing to ensure reliability
- Enhanced error handling for smoother operation
- Improved GUI for better user experience
- Modifications for carrier gateways to broaden compatibility (Verify they are all correct)

## Dependencies

This project requires the following dependencies:

- **Data247**: API for finding international carriers
- **javax Mail**: For sending emails via SMTP
- **JSON Simple**: For handling JSON data
