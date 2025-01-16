<!-- 

A CONSULTA FOI DESENVOLVIDA PARA O MYSQL. 
SE ESTIVER USANDO OUTRO BANCO DE DADOS A FUNÇÂO "TIMEDIFF" PODE NÃO FUNCIONAR.
NESSE CASO, PROCURE EM SEU BANCO DE DADOS POR FUNÇÃO SIMILAR.

-->
SELECT clientes.cpf,
	clientes.nome,
	clientes_tem_vagas.cor,
	clientes_tem_vagas.desconto,
	clientes_tem_vagas.marca,
	clientes_tem_vagas.modelo,
	clientes_tem_vagas.placa,
	clientes_tem_vagas.numero_recibo,
	clientes_tem_vagas.valor,
	vagas.codigo,
	clientes_tem_vagas.data_entrada,
	clientes_tem_vagas.data_saida,
    	TIMESTAMPDIFF(HOUR, data_saida, data_entrada) AS hours,
    	TIMESTAMPDIFF(MINUTE, data_saida, data_entrada) AS minutes
FROM clientes_tem_vagas
	INNER JOIN clientes ON 
	 clientes_tem_vagas.id_cliente = clientes.id 
	INNER JOIN vagas ON 
	 clientes_tem_vagas.id_vaga = vagas.id 
WHERE 
	 clientes.cpf = 'PREENCHA COM UM CPF PARA TESTE' 
 ORDER BY 	
 	clientes_tem_vagas.placa