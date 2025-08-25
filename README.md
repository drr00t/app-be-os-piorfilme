# Especificação do teste
Desenvolva uma API RESTful para possibilitar **a leitura da lista de indicados** e **vencedores da categoria** Pior Filme do Golden Raspberry Awards.

## objetivo
1.​ Obter o produtor com maior intervalo entre dois prêmios consecutivos
2. o que obteve dois prêmios mais rápido,

## Requisitos não funcionais do sistema 
1.  O web service RESTful deve ser implementado com base no nível 2 de maturidade de Richardson; 
2.  Devem ser implementados somente testes de integração. Eles devem garantir que os dados obtidos estão de acordo com os dados fornecidos na proposta; 
3.  O banco de dados deve estar em memória utilizando um SGBD embarcado (por exemplo, H2). Nenhuma instalação externa deve ser necessária; 
4.  A aplicação deve conter um readme com instruções para rodar o projeto e os testes de integração. 5.  
6.  O código-fonte deve ser disponibilizado em um repositório git (Github, Gitlab, Bitbucket, etc). 
 

## Sobre a aplicação

- A arquitetura da aplicação utiliza uma versão simplificada o BCE - Boundary - Control - Entity.
- Foi utilizado o básico de arquitetura limpa na separação de responsabilidades e pacotes:
  - boundary: contém as implementações usando tecnologias, também possui os contratos de dados usados na interface rest
  - control: possui apenas interfaces, optei por deixar o service **AwardWinsService** na boundary
  - entity: a aplicação não possui camada de entity, por não haver camada de domínio com regras de negócio, paenas contrato de dados, por isso optei por criar apenas os DTOs na boundary e a camada de entity para mapeamento com as fontes de dados.
- Os contratos de repositório foram implementados no **AgroalAwardNomineesRepository** por simplicidade
- Existem dois contratos de Repositórios, um para a carga do arquivo na inicialização usado no serviço **CsvLoadAwardNomineesLoader** um para consultas usado no **AwardWinsService**
- Existe apenas um controller implementado em **AwardResource**

### Estrutura de diretórios **svc-rest-priofilme** 

```console
    └── svc-rest-piorfilme
        ├── data
        │   ├── Movielist_Full.csv
        │   └── Movielist_Test.csv
        ├── pom.xml
        ├── README.md
        ├── src
        │   ├── main
        │   │   ├── docker
        │   │   │   ├── Dockerfile.jvm
        │   │   │   ├── Dockerfile.legacy-jar
        │   │   │   ├── Dockerfile.native
        │   │   │   └── Dockerfile.native-micro
        │   │   ├── java
        │   │   │   └── com
        │   │   └── resources
        │   │       ├── application.properties
        │   │       └── db
        │   └── test
        │       ├── java
        │       │   └── com
        │       └── resources
        │           └── application.properties
        └── target
            ├── build-analytics-event.json
            ├── build-metrics.json
            ├── classes
            ├── generated-sources
            ├── generated-test-sources
            ├── maven-archiver
            ├── maven-status
            ├── quarkus
            ├── quarkus-app
            ├── quarkus-artifact.properties
            ├── surefire-reports
            ├── svc-rest-piorfilme-1.0.0-SNAPSHOT.jar
            └── test-classes

    18 directories, 18 files
```

### Para executar a aplicação

Algumas observações:

- data: diretório que contém os arquivos csv com dados de entrada para carregamento na inicialização
- application.properties: os arquivos properties com as configurações dos modos test e prod estão nos respectivos paths src/main/resources e src/test/resources
- Movielist_Full.csv e Movielist_Test.csv: seus nomes são definidos na variável **csv.file.path** definida no resources
- Banco em memória: nenhum dados é salvo em disco, basta reiniciar o serviço e os arquivos serão carregados novamente


```console
    cd svc-rest-piorfilme
```

### Pré-requisitos

- Java 21+
- Maven 3.8.4+

### Tecnologias utilizadas
- Quarkus
- Sqlite
- Agroal
- apache commons-csv

### Execução dos testes integrados

```console
mvn quarkus:dev
```

### Execução direta da aplicação usando a infra do Quarkus

```console
    mvn clean package
    java -jar target/quarkus-app/quarkus-run.jar
```

### Acessando a rota 

```console
    curl -X GET http://127.0.0.1:8080/awards/producers/intervals/max-and-min

    {
    "min":[
        {"producer":"Frank Yablans","interval":1,"previousWin":1981,"followingWin":1982},
        {"producer":"Joel Silver","interval":1,"previousWin":1990,"followingWin":1991}
    ]
    "max":[
        {"producer":"Matthew Vaughn","interval":13,"previousWin":2002,"followingWin":2015}
        ]
    }
```
