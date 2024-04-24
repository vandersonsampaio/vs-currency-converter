CREATE TABLE tb_user_conversion (
   id_conversion VARCHAR(36) NOT NULL,
   id_user BIGINT NOT NULL,
   cd_source_currency VARCHAR(3) NOT NULL,
   vl_source_amount DECIMAL(18, 2) NOT NULL,
   cd_target_currency VARCHAR(3) NOT NULL,
   vl_target_amount DECIMAL(18, 2) NOT NULL,
   vl_rate_source DECIMAL(18, 7) NOT NULL,
   vl_rate_target DECIMAL(18, 7) NOT NULL,
   vl_rate_compose DECIMAL(18, 7) NOT NULL,
   dt_register TIMESTAMP NOT NULL,
   CONSTRAINT pk_user_conversion PRIMARY KEY (id_conversion)
);