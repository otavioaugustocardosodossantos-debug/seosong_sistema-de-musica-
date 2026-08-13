package com.seosong.integrado.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "artista")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do artista é obrigatório")
    @Column(name = "nome_artista", nullable = false, length = 150)
    private String nomeArtista;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_formacao")
    private LocalDate dataFormacao;

    @Column(length = 255)
    private String foto;

    @OneToMany(mappedBy = "artista", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Album> albuns = new ArrayList<>();

    @OneToMany(mappedBy = "artista", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Musica> musicas = new ArrayList<>();
}