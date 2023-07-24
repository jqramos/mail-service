###### **Send Email API - README**

This document provides details on how to use the "Send Email" API, which allows you to send emails using a JSON request body via an HTTP POST method. The API is implemented using Java with Spring WebFlux and leverages the Mailgun and Mailjet email service to deliver emails.

To run the application, you need to have Java 17 installed on your machine. 

run mvn clean install

**API Endpoint**

_POST /api/send-email_

Request Body
The request body should be in JSON format with the following parameters:

**subject**: The subject of the email.Type is String. 

**body**: The content of the email. Type is String.

**to**: The email address of the recipient. If not specified, the email will be sent to the default recipient. Type is array string.

**cc**: The email address of the recipient in the CC field.Type is array string.

**bcc**: The email address of the recipient in the BCC field.Type is array string.

Example JSON request body:

`{
    "subject": "Test Email",
    "body": "Hello ."
    "to": [ "test643453@mailinator.com" ]
}`


email addresses for receiving using [mailinator.com]():

test643453@mailinator.com :
https://www.mailinator.com/v4/public/inboxes.jsp?to=test643453

Todo

- Add Unit Tests
- Use object for responses
- better Error handling
- Add logging
- Improve payload validation
- store messages in constants


