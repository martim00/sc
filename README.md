para rodar em cima do tablelize-it

1. clonar o repositório do tablelize-it em algum diretório:

> cd c:/workspace
> git clone https://github.com/martim00/tablelize_it.git

2. clonar o repositório do scova em algum diretório

> cd c:/workspace
> git clone https://github.com/martim00/scova.git

3. compilar o tablelize-it

> cd c:/workspace/tablelize_it
> ant

4. modificar o arquivo build.properties do diretório c:/workspace/scova/build inserindo os diretórios relativos ao teu computador para o tablelize it

Ex:

<!--properties for tablelize-it -->

# classpath do teste instrumentado
test.classpath=c:/sc/tablelize/java/build/classes;

# diretório fonte
project.src=C:/tablelize 

# diretório aonde será gerado o código instrumentado
project.output=C:/sc/tablelize 

# classpath do projeto original (não instrumentado)
project.classpath=C:/tablelize/java/build/classes 

# diretório aonde estão os testes (.java) do código instrumentado
test.home=c:/sc/tablelize/java/src 


5. rodar o scova em cima do tablelize-it

> ant


6. (opcional) gerar o relatório

> ant report

irá gerar um arquivo report.html no diretório c:/workspace/scova/build

