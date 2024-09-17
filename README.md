Full encompassing Spring Book project for the resume for https://github.com/SanctusFides/

Requirements to run this app:

  Google Developer Account: Required to enable Google OAuth logins for users
  -  Add your account's client ID and secret key under applications.yml or remove the security block from the file and only use regular user/pass login methods
    
  PostgreSQL Database: Required for storing all application objects and user accounts
  
  - URL and the admin account details are input under the application.yml

  The application.yml file is preloaded with environment variables that can be used or plain text sources can be used to replace them.


  Cloudinary Account: Required for image hosting
  
  - Set in the environment using "CLOUDINARY_URL" as the variable (recommended) or the commented out preset string in the SecurityConstants file
  - Within Cloudinary's Media Explorer, create a folder titled "recipes" where images will be uploaded
  - Enter your account's image url into the CloudinaryImpl file where the placeholder variable is currently listed as "PLACHOLDER"
    
