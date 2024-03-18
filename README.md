# Semi Automated UPI Payment Gateway

## Endpoints

- **/post:**
  - Accepts POST requests and returns a response with the received data.
  - Accepts GET requests to display the form for user input without page refresh.

- **/sendData:**
  - Accepts POST requests with data, sends it to another server, and returns the response for dynamic QR code generation.

- **/generateQRCode:**
  - Accepts POST requests with an amount, generates a QR code for the UPI payment, and returns the base64 encoded image of the QR code.

- **/qr:**
  - Endpoint for testing and demo purposes to check dynamically generated QR codes before implementation.

- **/pay:**
  - Main endpoint displaying user transaction history and allowing fund deposits.

- **/sendEmail:**
  - Accepts POST requests to send emails.

- **/fetchEmail:**
  - Endpoint to fetch emails.

- **/payment:**
  - Shows QR code where user can enter amount, UTR, and email for payment.

- **/mail:**
  - Form to send receiving email.

- **/success:**
  - Redirects to successful payment page.

- **/requesterror:**
  - Redirects to failed payment page.

- **/save-utr:**
  - Saves transaction details and checks for existing transactions.

## Workflow

### Submitting Data

1. User fills out form with data and submits.
2. Form data is sent to /sendData via POST request.
3. Application sends data to another server using WebClient and receives response.
4. Response is displayed to user.

### Generating QR Code

1. User enters amount for UPI payment.
2. Amount is sent to /generateQRCode via POST request.
3. Application generates QR code for UPI payment URL with specified amount.
4. QR code image is converted to base64 encoding.
5. Base64 encoded image is returned as response and displayed to user.

### Verification

1. User scans QR code and initiates payment.
2. After payment, user is directed to next page /payment.
3. On /payment, user enters transaction ID (TX ID) and email for confirmation.
4. Data is submitted to /save-utr endpoint.
5. Application verifies transaction details using provided Amount and TX ID.
6. If details are correct, payment is confirmed and success page is displayed and confirmation mail will be sent on the submitted email.
7. If details are incorrect, user is redirected to error page and failure mail will be sent on the submitted email.

## Workflow Diagram

[User] --(submit data)--> [Form] --(POST /sendData)--> [Backend] --(WebClient)--> [External Server]
                                         |
                                         |     [Backend] --(POST /generateQRCode)--> [Backend]
                                         |       |
                                         |       V
                                         |   [QR Code Generation]
                                         |
                                         |
                                    [Response] --(QR Code Scan)--> [Payment]
                                         |
                                         |     [Backend] --(POST /save-utr)--> [Backend]
                                         |       |
                                         |       V
                                         |   [Verification]
                                         |
                                         |
                          [Success/Error Page]
