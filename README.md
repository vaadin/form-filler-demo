# Vaadin Form Filler demo project 

This project is a showcase example of using [Vaadin Form Filler add-on](https://github.com/vaadin/form-filler-addon) and helps to automate a filling in the order information.
Users can input their raw unstructured text and ask the application to auto-fill the form on the right side.

`main` branch uses Form Filler 1.0.0 pre-release and based on Vaadin 24.2 pre-release.
`24.1` branch uses latest stable Vaadin 24.1 version and Form filler 0.1.

Please note that the Form Filler add-on is currently an experimental feature. 
It may be removed, altered, or limited to commercial subscribers in future releases.

## Running the Application

To run the application you will need a valid ChatGPT API key.
You can get it by registering on [OpenAI website](https://platform.openai.com/overview).
Choose 'Sign up' and follow the instructions.

This key can be set as system property with '-D' flag or as an environment variable:

- System property: add `-DOPENAI_TOKEN=your_key` to your command in console.
- Environment variable:
   - Macos: include on your .zprofile
   ```script
   export OPENAI_TOKEN="THE KEY"
   ```
   - Windows: Use "System -> Advanced Settings -> Set Environment Variables" to set OPENAI_TOKEN

The project is a standard Maven project. To run it from the command line, type `mvnw` (Windows) or `./mvnw` (macOs/Unix) and open http://localhost:8080 in your browser.
With a system property it would be `mvnw -DOPENAI_TOKEN=your_key` (Windows) or `./mvnw -DOPENAI_TOKEN=your_key` (macOs/Unix).
This will run the project in [development mode](https://vaadin.com/docs/latest/configuration/development-mode).

You can also import the project to your IDE of choice as you would with any
Maven project. Read more on [how to set up a development environment for
Vaadin projects](https://vaadin.com/docs/latest/guide/install) (Windows, Linux, macOS).

When you run the application and open a browser, you will see the input area on the left and target form on the right.
Choose a order raw templates from the dropdown or type your own text, containing necessary key words like 'name', 'address', etc, which are needed for AI to properly process the text.

AI can also take instructions as input. These instructions tell AI what you want it to make extra with the form values, e.g. translate them all to Spanish.
Instructions can be given for a particular field or for all fields (context instructions).

When you're ready to start sending content to AI, press "Fill the form" button.
It might take a few seconds until AI responses back, you would see the loading indicator on the top.
Finally, you shall see the values in the form fields and also orders list on the bottom of the page.

You can play with the input text (remove parts, make a syntax mistakes) and see how AI will interpret them.

### Running in production mode
To run the application in [production mode](https://vaadin.com/docs/latest/production), you need:
- build the jar with `mvnw clean package -Pproduction` (Windows) or `./mvnw clean package -Production` (macOs/Unix)
- run jar with `java -DOPENAI_TOKEN=your_key -jar target/formfillerdemo-1.0-SNAPSHOT.jar`

### Running Integration Tests

Integration tests are implemented using [Vaadin TestBench](https://vaadin.com/testbench). The tests take a few minutes to run and are therefore included in a separate Maven profile. We recommend running tests with a production build to minimize the chance of development time toolchains affecting test stability. To run the tests using Google Chrome, execute

Windows: `mvnw verify -Pit,production`
macOs/Unix: `./mvnw verify -Pit,production`

and make sure you have a valid TestBench license installed (you can obtain a trial license from the [trial page](https://vaadin.com/trial)).

## Project structure

The project follow Maven's [standard directory layout structure](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html):
- Under the `srs/main/java` are located Application sources
    - `Application.java` is a runnable Java application class and the app's starting point
    - `InstructionsDialog.java` is dialog for adding instructions for AI
    - `FormFillerTextView.java` is an example of using Form Filler on the view
- Under the `srs/test` are located the TestBench test files
- `src/main/resources` contains configuration files and static resources, including text templates of orders
- The `frontend` directory in the root folder contains client-side
  dependencies and resource files. Example CSS styles used by the application
  are located under `frontend/styles`
