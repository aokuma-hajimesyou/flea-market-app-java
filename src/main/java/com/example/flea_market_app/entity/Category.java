package com.example.flea_market_app.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "category")
@Getter // @Dataから変更（無限ループ防止のため推奨）
@Setter // @Dataから変更
@NoArgsConstructor
@AllArgsConstructor
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	// 自己参照：親カテゴリーへの参照
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	@ToString.Exclude
	@JsonIgnore // 重要：JSON出力時に親の情報を含めないことで無限ループを回避します
	private Category parent;

	// 自己参照：子カテゴリーのリスト
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	@JsonIgnore // 今回のAjaxでは「その階層のリスト」だけが欲しいため、子リストも一旦除外します
	private List<Category> children;
}