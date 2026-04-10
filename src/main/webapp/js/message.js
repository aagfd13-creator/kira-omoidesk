// ==========================================
// 1. 쪽지함 로드 (받은 쪽지 / 보낸 쪽지)
// ==========================================
function loadMessages(type) {
    // 탭 전환 UI 처리
    document.getElementById('message-write-area').style.display = 'none';
    document.getElementById('message-list-area').style.display = 'block';

    fetch(`/messageview?action=${type}`)
        .then(res => res.json())
        .then(list => {
            const area = document.getElementById("message-list-area");
            let html = "";

            if (!list || list.length === 0) {
                html = `<div style='text-align:center; padding:50px; color:#c0b0a0;'>쪽지가 없습니다. 텅~ 🍃</div>`;
            } else {
                // 쪽지가 있을 때 리스트 그리기
                list.forEach(m => {
                    const targetName = type === 'received' ? `From: ${m.target_nick}` : `To: ${m.target_nick}`;

                    html += `
                    <div style="border-bottom:1px dashed #f2c0bd; padding:10px 0; position:relative;">
                        
                        <div style="font-size:13px; color:#a29bfe; font-weight:bold; cursor:pointer;" 
                             onclick="goSearchMain('${m.target_pk}', '${m.target_nick}')">
                             
                            ${targetName} <span style="color:#c0b0a0; font-weight:normal; font-size:11px; margin-left:10px;">${m.m_date}</span>
                        </div>
                        
                        <div style="margin-top:8px; color:#5a4a3a; font-size:15px;">${m.m_content}</div>
                        <button onclick="deleteMsg('${m.m_pk}', '${type}')" style="position:absolute; right:5px; top:10px; border:none; background:none; cursor:pointer; color:#ff7675; font-size:14px;">✖</button>
                    </div>`;
                });
            } // 🚨 네 코드에서 이 괄호가 빠져있었다!

            area.innerHTML = html;
        }); // 🚨 네 코드에서 이 괄호도 빠져있었다!
}

// ==========================================
// 2. 쪽지 쓰기 화면 열기 (+ 일촌 목록 불러와서 select 태그에 넣기)
// ==========================================
function openWriteMessage(defaultTargetPk = "") {
    document.getElementById('message-list-area').style.display = 'none';
    document.getElementById('message-write-area').style.display = 'block';

    // 기존에 만들어둔 일촌 목록 API 재활용!
    fetch('/friendview?action=list')
        .then(res => res.json())
        .then(list => {
            const select = document.getElementById('msg-receiver-select');
            select.innerHTML = '<option value="">💌 받을 일촌을 선택하세요</option>';

            list.forEach(f => {
                // 특정 일촌에게 보내기를 눌렀을 경우 자동 선택되도록 처리
                const isSelected = (f.friend_pk === defaultTargetPk) ? 'selected' : '';
                select.innerHTML += `<option value="${f.friend_pk}" ${isSelected}>${f.u_nickname}</option>`;
            });
        });
}

// ==========================================
// 3. 쪽지 전송 (서버로 쏘기)
// ==========================================
function sendMessage() {
    const receiverPk = document.getElementById('msg-receiver-select').value;
    const content = document.getElementById('msg-content').value;

    if (!receiverPk) {
        alert('받을 사람(일촌)을 선택해주세요!');
        return;
    }
    if (!content.trim()) {
        alert('내용을 입력해주세요!');
        return;
    }

    const params = new URLSearchParams({action: 'send', receiverPk: receiverPk, content: content});

    fetch('/messageaction', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: params
    })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                alert("쪽지가 슝~ 전송되었습니다! 🚀");
                document.getElementById('msg-content').value = ''; // 입력창 비우기
                loadMessages('sent'); // 보낸 쪽지함으로 자동 이동
            } else {
                alert(data.message || "발송 실패");
            }
        });
}

// ==========================================
// 4. 쪽지 삭제
// ==========================================
function deleteMsg(msgPk, type) {
    if (!confirm("이 쪽지를 삭제할까요? (상대방의 쪽지함에서는 지워지지 않습니다)")) return;

    const params = new URLSearchParams({action: 'delete', msgPk: msgPk, type: type});
    fetch('/messageaction', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: params
    })
        .then(res => res.json())
        .then(data => {
            if (data.success) loadMessages(type); // 삭제 성공 시 리스트 갱신
            else alert("삭제 실패!");
        });
}