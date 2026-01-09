const imageInput = document.getElementById('imageInput');
    const imagePreview = document.getElementById('imagePreview');
    const newPreviewContainer = document.getElementById('new-preview-container');
    const existingPreview = document.getElementById('existing-preview');

    imageInput.addEventListener('change', function(e) {
        const file = e.target.files[0];
        
        if (file) {
            const reader = new FileReader();
            
            reader.onload = function(e) {
                // imgタグに画像ソースをセット
                imagePreview.src = e.target.result;
                // 新しいプレビューを表示
                newPreviewContainer.style.display = 'block';
                
                // もし編集用の「既存画像」があれば非表示にする（混乱を防ぐため）
                if (existingPreview) {
                    existingPreview.style.display = 'none';
                }
            }
            
            reader.readAsDataURL(file);
        } else {
            // ファイルが選択解除された場合
            newPreviewContainer.style.display = 'none';
            if (existingPreview) {
                existingPreview.style.display = 'block';
            }
        }
    });

	document.addEventListener('DOMContentLoaded', function() {
				const btn = document.getElementById('notificationBtn');
				const modal = document.getElementById('notificationModal');

				if (btn && modal) {
					// ベルマーククリックで表示切り替え
					btn.addEventListener('click', function(e) {
						e.stopPropagation(); // 親要素へのクリック伝播を阻止
						const isDisplayed = modal.style.display === 'block';
						modal.style.display = isDisplayed ? 'none' : 'block';
					});

					// モーダル以外をクリックしたら閉じる
					document.addEventListener('click', function(e) {
						if (!modal.contains(e.target) && e.target !== btn) {
							modal.style.display = 'none';
						}
					});
				}
			});