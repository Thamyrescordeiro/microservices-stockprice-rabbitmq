const amqp = require('amqplib')

const fila = 'PRICE'

amqp.connect({
    host: 'localhost',
    port: 5672,
    username: 'guest',
    password: 'guest'
})
    .then((conection) => {
        conection.createChannel()
            .then((canal) => {
                canal.consume(fila, (message) => {
                    console.log(message.content.toString())
                }, {noAck: true})
            })
            .catch((erro) => console.log(erro))
    })
    .catch((erro) => console.log(erro))