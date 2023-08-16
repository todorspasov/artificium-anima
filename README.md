# artificium-anima - Integrating AI, Discord & Slack

## How to build and run
1. Clone the repo:
```
git clone https://github.com/todorspasov/artificium-anima.git
```  
2. Navigate in the artificium-anima folder
```
cd artificium-anima
```
3. Add your API tokens in src\main\resources\application.properties
```
openai.token=sk-.....
discord.bot.token=....
slack.bot.token=...
slack.bot.channel=...
```
4. Run:
```
gradlew clean build bootJar
```
5. Log into the discord server where the bot is in
6. To list all of the supported commands type:
```
!help
```
7. Type a command. Example:
```
!chatgpt-role Act as a hundred year old wise prophet
!chatgpt What will be the future of mankind after a hundred years?
!chatgpt And which of the current religions will remain then?
!chatgpt before I leave, one final question: Will AI become a natural part of the human body?
!chatgpt-forget
!chatgpt Will AI become a natural part of the human body?
```
