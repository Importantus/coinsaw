<p align="center">
<img src="https://github.com/user-attachments/assets/303e72c0-8689-4e22-917d-7c1830be885a" alt="The logo of the coinsaw app" width=20% />

<h1 align="center">Coinsaw</h1>
<p align="center">Expense splitting for groups. Nothing more.</p>

<p align="center">
<!-- <img alt="GitHub Actions Workflow Status" src="https://img.shields.io/github/actions/workflow/status/Importantus/coinsaw/build.yml"> -->
<!-- <img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/Importantus/coinsaw"> -->
<img alt="GitHub" src="https://img.shields.io/github/license/Importantus/coinsaw">
</p>

> [!WARNING]
> This app is still under heavy development and not ready for production use!

Coinsaw is an app used to track and split expenses within groups. It works fully offline but allows for optional [synchronization with a server](https://github.com/Importantus/coinsaw-backend).

I built this app mainly to learn Jetpack Compose and as an alternative to Splitwise (which costs money since autumn 2023).

<img alt="Mockup_Coinsaw" src="https://github.com/user-attachments/assets/9f0817ce-4188-4d3f-9217-ca95b9e9faeb">

## How it works

The app is organized in groups. You can create a group and add as many members as you want. These users are not linked to an account and are scoped to the group. You can then add expenses to the group and split them between the members. The app will calculate how much each member owes the others. That's basically it.

## Sync

Whenever you perform actions in a group (add a member, rename the group, or add or edit an expense), it updates the local database (as you would know from other apps). Each of these actions is also saved in a changelog. When you sync the group, the app will send the changelog to the server and download the changelog of the other members. This way, the app can merge the changes and keep the data in sync.

### Server

You can find the server [here](https://github.com/Importantus/coinsaw-backend). Since I don't have the resources or legal knowledge to run a public server, I can't provide a public instance. However, you can easily run your own instance with Docker. You can set the server URL per group when you enable syncing.

### Auth

As mentioned earlier, the app doesn't require an account. Instead, when you enable syncing, you can create share tokens that you can share with other members. These tokens are used to create new sessions that then authenticate the clients. You can set per share token how often it can be used and if the sessions that are created with it are admin sessions. Admin sessions can edit the group, add or remove shares, and manage other sessions.

## Contributing

If you wish to contribute, feel free to open an issue or a pull request. I appreciate any help and tips, especially since this is my first project for Android. If you want to contribute to the server, you can find the repository [here](https://github.com/Importantus/coinsaw-backend).

### Encryption

I want to add end-to-end encryption to the app, but I'm unsure how to do it securely. I lack expertise in security and don't want to implement something that is not secure. If you have any ideas or would like to help, please open an issue or a pull request. 