import dotenv from 'dotenv';
dotenv.config();
import axios from 'axios';
import { Client, GatewayIntentBits, Partials, Events, TextChannel, ButtonStyle, ButtonBuilder, ActionRowBuilder, ButtonInteraction } from 'discord.js';
import crypto from 'crypto';
import bunyan from 'bunyan';

const log = bunyan.createLogger({ name: 'wallet-verification-discord-bot' });

const {
    DISCORD_TOKEN,
    BACKEND_BASE_URL,
    FRONTEND_URL,
    BACKEND_BASIC_AUTH_PASSWORD,
    BACKEND_BASIC_AUTH_USER,
    VERIFIED_ROLE_ID,
    CHANNEL_ID,
    GUILD_ID,
    DISCORD_VERIFICATION_BOT_SALT
} = process.env;

const token = Buffer.from(
    `${BACKEND_BASIC_AUTH_USER}:${BACKEND_BASIC_AUTH_PASSWORD}`,
    'utf8'
).toString('base64');
const authenticationHeader = {
    headers: {
        'x-cf-login-system': 'BASIC',
        Authorization: `Basic ${token}`,
    },
};

const client = new Client({
    partials: [Partials.Channel],
    intents: [
        GatewayIntentBits.Guilds,
        GatewayIntentBits.GuildMessages,
        GatewayIntentBits.MessageContent,
        GatewayIntentBits.DirectMessages,
        GatewayIntentBits.DirectMessageTyping,
    ],
});

const generateAuthenticationSecret = () => {
    const randomSecret = (Math.random() + 1)
        .toString(36)
        .replace('1.', '');

    return randomSecret;
};

client.login(DISCORD_TOKEN);

client.once(Events.ClientReady, async (client) => {
    log.info(`Logged in as ${client.user.tag}!`);

    const channel: TextChannel = client.channels.cache.get(CHANNEL_ID) as TextChannel;

    if (channel.isTextBased) {
        const messages = await channel.messages.fetch();
        const hasBotPostedInChannel = messages.some(message => message.author.id === client.user.id);
        if (!hasBotPostedInChannel) {
            const button = new ButtonBuilder()
                .setLabel('Start Wallet Verification')
                .setCustomId('start_wallet_verification')
                .setStyle(ButtonStyle.Success);

		    const actionRow = new ActionRowBuilder<ButtonBuilder>().addComponents(button);

            channel.send({
                content: 'Click the button below to verify your wallet',
                components: [actionRow],
            })
        } else {
            console.log('The bot has already posted the button into this channel');
        }
    }
});

client.on(Events.InteractionCreate, async (interaction) => {
    if (interaction.user.bot) return;

    if (interaction.isButton) {
        interaction = (interaction as ButtonInteraction);
        try {
            const discordUserId = interaction.user.id;
            const guild = await client.guilds.fetch(GUILD_ID);

            try {
                const guildMember = await guild.members.fetch({
                    user: discordUserId,
                    force: true,
                });

                if (!guildMember.roles.cache.hasAny(VERIFIED_ROLE_ID)) {
                    interaction.reply({
                        content: `Hi ${interaction.user.username}, your need to accept our terms and conditions by reacting with a 🚀 emoji to the message within the verification channel. Click the button again once you have accepted the terms and conditions.`,
                        ephemeral: true,
                    });
                    return;
                }
            } catch (error) {
                log.error((error as Error).message);
                interaction.reply({
                    content: `Hi ${interaction.user.username}, something went wrong. Please try again later.`,
                    ephemeral: true
                });
                return;
            }

            const hashedDiscordId = crypto
                .createHash('sha256')
                .update(DISCORD_VERIFICATION_BOT_SALT + discordUserId)
                .digest('hex');

            const response = await axios.get(`${BACKEND_BASE_URL}/api/discord/user-verification/is-verified/${hashedDiscordId}`, authenticationHeader);

            if (response.data.verified) {
                interaction.reply({
                    content: `Hi ${interaction.user.username}, you have already verified your wallet!`,
                    ephemeral: true
                });
                return;
            }

            const randomSecret = generateAuthenticationSecret();
            try {
                const startVerificationResponse = await axios.post(`${BACKEND_BASE_URL}/api/discord/user-verification/start-verification`, {
                    discordIdHash: hashedDiscordId,
                    secret: randomSecret,
                }, authenticationHeader);

                if (startVerificationResponse.status !== 200) {
                    interaction.reply({
                        content: `Hi ${interaction.user.username}, something went wrong. Please try again later.`,
                        ephemeral: true
                    });
                    return;
                }

                const button = new ButtonBuilder()
                    .setLabel('Finish Wallet Verification')
                    .setURL(`${FRONTEND_URL}?action=verification&secret=${hashedDiscordId}|${randomSecret}`)
                    .setStyle(ButtonStyle.Link);

                const actionRow = new ActionRowBuilder<ButtonBuilder>().addComponents(button);
                await interaction.reply({
                    content: `Thank you ${interaction.user}, please click that button to finish the verification process in Cardano Ballot!`,
                    ephemeral: true,
                    components: [actionRow],
                });
            } catch (error) {
                log.error((error as Error).message);
                interaction.reply({
                    content: `Hi ${interaction.user.username}, something went wrong. Please try again later.`,
                    ephemeral: true
                });
            }
        } catch (error) {
            console.log(error);
        }
    }
});
