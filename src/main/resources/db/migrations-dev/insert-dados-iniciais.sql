-- Inserir Usuário ADMIN (senha: admin123 - hash BCrypt)
INSERT INTO usuarios (nome, email, senha, role, ativo)
VALUES ('Administrador', 'admin@erp.com', '$2a$10$VgRO0aUGiI3kd5KqktObz.MEWVA.4f0QqOHk1PaX8uVkMjsH5m12C', 'ADMIN', true);

-- Inserir Fornecedor Exemplo
INSERT INTO fornecedores (nome, documento, telefone, email, ativo)
VALUES ('Fornecedor Exemplo Ltda', '12345678000199', '(11) 99999-9999', 'contato@fornecedor.com', true);

-- Inserir Cliente Exemplo
INSERT INTO clientes (nome, documento, email, telefone, ativo)
VALUES ('Cliente Teste', '12345678901', 'cliente@teste.com', '(11) 98888-8888', true);

-- Inserir Produto Exemplo (Assumindo fornecedor_id = 1)
INSERT INTO produtos (nome, descricao, codigo_barras, preco, fornecedor_id)
VALUES ('Produto de Teste', 'Produto de exemplo para ambiente DEV', '7891234567890', 99.90, 1);

-- Inserir Estoque para Produto Exemplo (Assumindo produto_id = 1)
INSERT INTO estoque (produto_id, quantidade)
VALUES (1, 100);

-- Inserir Pedido Exemplo (Assumindo cliente_id = 1)
INSERT INTO pedidos (cliente_id, data_pedido, status, total)
VALUES (1, now(), 'ABERTO', 199.80);

-- Inserir ItensPedido (Pedido ID 1, Produto ID 1)
INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, preco_unitario)
VALUES (1, 1, 2, 99.90);