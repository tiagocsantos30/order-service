# Order-service

## O que é este projeto?

Este projeto tem como objetivo prover a integração com Produto Externo A e Produto Externo B

A fim de facilitar a integração com os produtos externos, este projeto disponibiliza a criação de uma exchange vinculada a duas filas
para cada produto externo. A exchange é responsável por receber as mensagens e distribuí-las para as filas. As filas, por sua vez, são
responsáveis por armazenar as mensagens até que sejam processadas.

Para simular a ingestão de dados pelo Produto Externo A foi criado a classe StartupOrderGenerator que simula a ingestão de mensagens na fila orderReceivedQueue (simulando ProdutoExterno A). Após isso, são processadas todas as ordens vinculadas aos produtos realizando o somatório dos valores, verificando se existem registros em duplicidade. Em seguida, são armazenadas as informações na base MySql e enviadas para a fila orderProcessedQueue.

Para suportar a alta demanda de mensagens, o projeto utiliza o RabbitMQ como broker de mensagens. O RabbitMQ é um software de mensageria open-source que implementa o protocolo AMQP (Advanced Message Queuing Protocol) e é amplamente utilizado em aplicações distribuídas para garantir a comunicação entre os componentes.
O Executor de Tarefas é configurado com um pool de threads para processar as mensagens de forma assíncrona. 
O pool de threads é configurado com 20 threads iniciais e suporta até 100 threads para escalar em picos de carga, garantindo que o sistema continue a processar mensagens rapidamente.
O Executor de Tarefas também é configurado com uma fila de 500 tarefas, equilibrando o fluxo de mensagens e evitando a saturação do sistema.

Para simular o processamento de mensagens foi simulado a ingestão e processamento de 100.000 mensagens utilizando VisualVM obtivemos o seguinte resultado:
* Durante todo o processamento o nosso Heap ficou estável em 50MB com isso garantimos que não teremos problemas de OutOfMemoryError e indisponibilidade do sistema.


## Tecnologias
- Spring Boot como framework de aplicação
- String Data JPA para acesso ao banco de dados
- RabbitMQ
- MySql
- Docker
- Junit