# Coinsaw

> This app is still under heavy development and not ready for production use!

Coinsaw is an app to track and split expenses in groups. It works fully offline but allows for
optional [synchronization with a server](https://github.com/Importantus/coinsaw-backend).

I build this app mainly to learn Jetpack Compose and as an alternative to Splitwise (that [costs
money](https://feedback.splitwise.com/knowledgebase/articles/2010350-why-am-i-seeing-an-expense-limit) since
autum 2023). 

## How it works

The app is organized in groups. You can create a group and add as many members as you want. These users are not linked to any account and are scoped to the group. You can then add expenses to the group and split them between the members. The app will calculate how much each member owes to the others and display it in a simple list. That's basically it.

## Sync

When you perform actions in a group (add a member, rename the group or add or edit an expense) updates the local database (as you now it from other apps). But each of these actions is also saved in a changelog. When you sync the app, it will send the changelog to the server and download the changelog of the other members. This way, the app can merge the changes and keep the data in sync.

### Server

Like this app, the server is also open source. You can find it [here](https://github.com/Importantus/coinsaw-backend). As I don't have the ressources or legal knowledge to run a public server, I can't provide a public instance. But you can run your own instance. You can set the server-url per group when you enable the syncing. 

### Auth

As mentioned above, the app doesn't require any account. But when you enable the syncing, you can create share tokens that you can share with other members. These tokens are used to authenticate the members. You can set per share token how often it can be used and if the sessions that are created with it are admin sessions. Admin sessions can edit the group and add or remove shares and other sessions.

## Contributing

If you want to contribute, you can open an issue or a pull request. I'm happy about any help. If you want to contribute to the server, you can find the repository [here](https://github.com/Importantus/coinsaw-backend). 

### Encryption

I really want to add end-to-end encryption to the app. But I'm not sure how to do it securely. I'm not a security expert and I don't want to implement something that is not secure. If you have any ideas or want to help, please open an issue or a pull request.