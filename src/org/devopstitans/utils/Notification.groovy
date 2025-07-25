package org.devopstitans.utils

class Notification implements Serializable {
    static void sendSlack(String message, String channel) {
        slackSend(
            channel: "#${channel}",
            message: message,
            color: 'good'
        )
    }
}

