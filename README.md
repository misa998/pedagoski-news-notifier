Webscraping app that looks for news, stores them and notifies subscribers. It was written for a specific website, deployed and scheduled to run three times a day.

Database is required since it will store the old news and check when new one appears.

Database parameters and sender email account can be set in app.properties file.

When news are found, app will filter them through black list and notify subscribers. Errors will be sent to an account set in global exception handler (ExceptionNotifier.java).
