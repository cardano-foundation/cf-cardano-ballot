# 🤖 Discord Verification Bot 🤝

This bot aims to provide a wallet and user association based on the user's discord account.

## 🦾 Getting Started

1. Add an empty .env file beside this README.md file
2. [Create a discord app](https://discord.com/developers/docs/getting-started)
3. Click on OAuth2 and press the "Reset Secret" button to get a token and copy it into the .env file as `DISCORD_TOKEN=<token>`
4. Goto the "URL Generator" (submenu item of OAuth2) and activate the "bot" scope
5. Within the bot perimssion section (below) check `Read Messages/View Channels`, `Send Messages`, `Embed Links` and `Read Message History`
6. Copy the generated URL and open it in a new tab
7. Select the server you want to add the bot to and press "Authorize"
8. `OPTIONAL` Change the verification level to "Highest" in the server settings under "Safety Setup" to make sure a user can only link accounts to wallets that have a verified phone number on Discord
9. In the Discord user settings under "Advanced" enable "Developer Mode" (this is needed to get the server and channel id)
10. Right click on the server and copy the server id and add it to the .env file as `GUILD_ID=<server id>`
11. Create a new channel e.g. "🔗wallet-verification" which is only visible for verified users and make sure only the bot can send messages in this channel
12. Right click on the channel and copy the channel id and add it to the .env file as `CHANNEL_ID=<channel id>`
13. If you don't have it yet, create a new role e.g. "Verified" that a user gets after accepting the rules of your server
14. Right click on the role and copy the role id and add it to the .env file as `VERIFIED_ROLE_ID=<role id>`
15. The bot will send a message to a backend you would need to provide. Please make sure to implement the endpoints as described below in the "Endpoints" section
16. Provide the backend within the .env file as `BACKEND_BASE_URL=<backend url>`
17. We assume that the backend is basic auth protected. Please provide the username and password within the .env file as `BACKEND_BASIC_AUTH_USER=<username>` and `BACKEND_BASIC_AUTH_PASSWORD=<password>`
18. Run `npm install` to install all dependencies
19. Run `npm run start` to start the bot

### 👀 Endpoints

- `GET` /api/discord/user-verification/is-verified/<hashedDiscordId> - Checks if a user is verified and return `verified: true` or `verified: false`
- `POST` /api/discord/user-verification/start-verification - Starts the verification process and returns 200 if successful
This endpoint expects a body like this, which needs to be stored for instance in a database:
```json
{
    "discordIdHash": "<hashedDiscordId>",
    "secret": "<randomSecret>",
}
```
- `POST` /api/discord/user-verification/check-verification - This endpoint is called by the frontend to send signature along with the secret and hashed Discord id to the backend. The backend needs to check if the signature is valid and if the hashed Discord id and secret matches the one stored in the database.

## 🌱 Environment Variables

| Variable | Description |
| --- | --- |
| DISCORD_TOKEN | The token of the discord bot |
| GUILD_ID | The id of the discord server |
| CHANNEL_ID | The id of the discord channel |
| VERIFIED_ROLE_ID | The id of the verified role |
| BACKEND_BASE_URL | The base url of the backend |
| BACKEND_BASIC_AUTH_USER | The username of the basic auth |
| BACKEND_BASIC_AUTH_PASSWORD | The password of the basic auth |
| DISCORD_VERIFICATION_BOT_SALT | The salt used to hash the discord id |
| FRONTEND_URL | The url of the frontend. The bot will link the user to this url with secret and hashed Discord id as url parameters |

## 🏗 Development

We use a mix of tsc -w and nodemon to watch for changes and restart the bot. Just run the following command to start the bot in development mode:

```zsh
npm run dev
```