# Actions on Google: Dialogflow Docs Samples (Java)

This repo contains samples demonstrating the core features of Actions on Google
when building with [Dialogflow](dialogflow.com) and using our [Java/Kotlin Client Library](https://github.com/actions-on-google/actions-on-google-java).
Code snippets from these samples are included throughout the Actions on Google
[documentation](https://developers.google.com/actions).

### Setup Instructions
### Prerequisites
1. Download & install the [Google Cloud SDK](https://cloud.google.com/sdk/docs/)
1. [Gradle with App Engine Plugin](https://cloud.google.com/appengine/docs/flexible/java/using-gradle)
   + Run `gcloud auth application-default login` with your Google account
   + Install and update the App Engine component,`gcloud components install app-engine-java`
   + Update other components, `gcloud components update`

### Configuration
The commands shown below can be run from any of the directories to deploy
and run the sample.

#### Actions Console
1. From the [Actions on Google Console](https://console.actions.google.com/), New project (this will become your *Project ID*) > **Create project** > under **More options** > **Conversational**.
1. From the top menu under **Develop** > **Actions** (left nav) > **Add your first action** > **BUILD** (this will bring you to the Dialogflow console) > Select language and time zone > **CREATE**.
1. In Dialogflow, go to **Settings** ⚙ > **Export and Import** > **Restore from zip**.
1. Follow the directions to restore from the `agent.zip` in this sample's `dialogflow` directory.

#### App Engine Deployment & Webhook Configuration
When a new project is created using the Actions Console, it also creates a Google Cloud project in the background.
1. Configure the gcloud CLI and set your Google Cloud project to the name of your Actions on Google Project ID, which you can find from the [Actions on Google console](https://console.actions.google.com/) under Settings ⚙
   + `gcloud init`
1. Deploy to [App Engine using Gradle](https://cloud.google.com/appengine/docs/flexible/java/using-gradle):
   + `gradle appengineDeploy` OR
   +  From within IntelliJ, open the Gradle tray and run the appEngineDeploy task.

#### Dialogflow Console
Return to the [Dialogflow Console](https://console.dialogflow.com), from the left navigation menu under **Fulfillment** > **Enable Webhook**, set the value of **URL** to `https://${YOUR_PROJECT_ID}.appspot.com` > **Save**.
1. From the left navigation menu, click **Integrations** > **Integration Settings** under Google Assistant > Enable **Auto-preview changes** >  **Test** to open the Actions on Google simulator then say or type `Talk to my test app`.

### Running this Sample
+ You can test your Action on any Google Assistant-enabled device on which the Assistant is signed into the same account used to create this project. Just say or type, “OK Google, talk to my test app”.
+ You can also use the Actions on Google Console simulator to test most features and preview on-device behavior.

#### [dftest.sh](./dftest.sh)
There is a top-level [test script](./dftest.sh) that runs a smoke test against the Dialogflow agent
stored in each sample directory to ensure intents are configured for happy path flows. The script
checks a given sample's `dialogflow/test/dialog.csv` file to ensure each turn of dialog will trigger
the expected intent.

### References & Issues
+ Questions? Go to [StackOverflow](https://stackoverflow.com/questions/tagged/actions-on-google), [Assistant Developer Community on Reddit](https://www.reddit.com/r/GoogleAssistantDev/) or [Support](https://developers.google.com/actions/support/).
+ For bugs, please report an issue on Github.
+ Actions on Google [Documentation](https://developers.google.com/actions/extending-the-assistant)
+ [Webhook Boilerplate Template](https://github.com/actions-on-google/dialogflow-webhook-boilerplate-java) for Actions on Google.
+ More info about [Gradle & the App Engine Plugin](https://cloud.google.com/appengine/docs/flexible/java/using-gradle).
+ More info about deploying [Java apps with App Engine](https://cloud.google.com/appengine/docs/standard/java/quickstart).

### Make Contributions
Please read and follow the steps in the [CONTRIBUTING.md](CONTRIBUTING.md).

### License
See [LICENSE](LICENSE).

### Terms
Your use of this sample is subject to, and by using or downloading the sample files you agree to comply with, the [Google APIs Terms of Service](https://developers.google.com/terms/).
