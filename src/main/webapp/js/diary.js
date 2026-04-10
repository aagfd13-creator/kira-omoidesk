/**
 * [1] 다이어리 내용을 비동기로 불러오는 핵심 함수
 */
function loadDiary(url = "diary") {
    if (!url.includes("ajax=true")) {
        url += (url.includes("?") ? "&" : "?") + "ajax=true";
    }

    // 현재 페이지의 ownerId 유지 로직
    const currentOwner = document.getElementById("currentDiaryOwner")?.value;
    if (currentOwner && !url.includes("memberId=")) {
        url += "&memberId=" + currentOwner;
    }

    console.log("📬 요청 주소:", url);

    fetch(url)
        .then((response) => {
            if (!response.ok) throw new Error(`서버 응답 에러 (상태코드: ${response.status})`);
            return response.text();
        })
        .then((html) => {
            const contentArea = document.getElementById("notebook-content");
            if (contentArea) {
                contentArea.innerHTML = html;

                // ★ [성현님 요청 기능] 날짜 클릭 시 목록으로 부드럽게 스크롤
                // URL에 'd='이 있으면 날짜를 선택했다는 뜻이므로 목록 위치로 이동합니다.
                if (url.includes("d=")) {
                    // diary-board 클래스를 가진 요소(목록창)를 찾아서 그 위치로 이동
                    setTimeout(() => {
                        const board = document.querySelector(".diary-board");
                        if (board) {
                            board.scrollIntoView({ behavior: "smooth", block: "start" });
                        }
                    }, 50); // HTML이 완전히 렌더링될 시간을 아주 잠깐 줌
                } else {
                    // 그 외 일반 이동은 상단으로 스크롤
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                }
            }
        })
        .catch((error) => console.error("❌ 다이어리 로드 실패:", error));
}

/**
 * [2] 일기 작성
 */
function submitDiaryForm() {
    const form = document.getElementById('diaryWriteForm');
    if (!form) return;
    const formData = new FormData(form);
    const params = new URLSearchParams(formData);

    fetch('diary-write', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
        body: params
    })
        .then(response => response.text())
        .then(() => {
            loadDiary(`diary?y=${formData.get('d_year')}&m=${formData.get('d_month')}&d=${formData.get('d_date')}`);
        })
        .catch(error => console.error("일기 등록 실패:", error));
}

/**
 * [3] 일기 수정
 */
function updateDiaryForm() {
    const form = document.getElementById('diaryUpdateForm');
    if (!form) return;
    const formData = new FormData(form);
    const params = new URLSearchParams(formData);

    fetch('diary-update', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
        body: params
    })
        .then(response => response.text())
        .then(() => {
            loadDiary(`diary-detail?no=${formData.get('no')}&y=${formData.get('d_year')}&m=${formData.get('d_month')}&d=${formData.get('d_date')}`);
        })
        .catch(error => console.error("일기 수정 실패:", error));
}

/**
 * [4] 댓글 등록
 */
function submitReply(no, y, m, d) {
    const form = document.getElementById('replyWriteForm');
    const input = form.querySelector('input[name="r_txt"]');
    if (!input.value.trim()) { alert("댓글 내용을 입력해주세요! 😊"); input.focus(); return; }
    const formData = new FormData(form);
    const params = new URLSearchParams(formData);

    fetch('diary-reply-write', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
        body: params
    })
        .then(response => response.text())
        .then(() => {
            input.value = "";
            loadDiary(`diary-detail?no=${no}&y=${y}&m=${m}&d=${d}`);
        })
        .catch(error => console.error("댓글 등록 실패:", error));
}

/**
 * [5] 댓글 삭제
 */
function deleteReply(r_no, d_no, y, m, d) {
    if (!confirm("이 댓글을 정말 삭제할까요? 🗑️")) return;
    fetch(`diary-reply-delete?r_no=${r_no}`)
        .then(() => loadDiary(`diary-detail?no=${d_no}&y=${y}&m=${m}&d=${d}`))
        .catch(error => console.error("댓글 삭제 실패:", error));
}

// [보조 캘린더 로직]
let currentPickerYear = new Date().getFullYear();
function openQuickPicker(e) {
    e.stopPropagation();
    const picker = document.getElementById('quickDatePicker');
    if (picker) {
        picker.style.display = 'block';
        currentPickerYear = document.getElementById('quickYearSelect').value;
    }
}
function updateQuickYear(val) { currentPickerYear = val; }
function confirmQuickDate(month) {
    loadDiary(`diary?y=${currentPickerYear}&m=${month}`);
    const picker = document.getElementById('quickDatePicker');
    if (picker) picker.style.display = 'none';
}
window.addEventListener('click', function(e) {
    const picker = document.getElementById('quickDatePicker');
    const title = document.querySelector('.cal-title-click');
    if (picker && picker.style.display === 'block') {
        if (!picker.contains(e.target) && e.target !== title) {
            picker.style.display = 'none';
        }
    }
});