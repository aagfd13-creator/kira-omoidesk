document.addEventListener("DOMContentLoaded", function () {
    // 1. 처음 켜졌을 때 메인 화면(main.jsp) 로드
    loadPage('main.jsp');

    // 2. 메뉴/탭 버튼 클릭 이벤트 등록
    document.querySelectorAll('.menu-item, .nb-tab').forEach(button => {
        button.addEventListener('click', function() {
            const targetUrl = this.getAttribute('data-src');

            // 클릭한 탭 색상 활성화
            document.querySelectorAll('.menu-item, .nb-tab').forEach(el => el.classList.remove('active'));

            // 왼쪽 메뉴와 상단 탭 모두 동기화 처리 (선택사항)
            const correspondingTabs = document.querySelectorAll(`[data-src="${targetUrl}"]`);
            correspondingTabs.forEach(el => el.classList.add('active'));

            loadPage(targetUrl);
        });
    });

    //=============================================================================================
    // 검색창 js (DOMContentLoaded 안으로 이동)
    //=============================================================================================
    const searchInput = document.getElementById('live-search-input');
    const searchDropdown = document.getElementById('search-dropdown');

    if (searchInput && searchDropdown) {
        searchInput.addEventListener('input', function () {
            const keyword = searchInput.value.trim();

            if (keyword === "") {
                searchDropdown.classList.add('hidden');
                searchDropdown.innerHTML = '';
                return;
            }

            // 🚨 임시 테스트용 더미 데이터
            const dummyData = [
                {pk: "user1", nick: "동민", name: "김동민", title: "동민이의 소소한 일상"},
                {pk: "user2", nick: "코딩요정", name: "박자바", title: "버그 없는 청정구역"}
            ];

            renderDropdown(dummyData);
        });

        function renderDropdown(users) {
            searchDropdown.innerHTML = '';

            if (users.length === 0) {
                searchDropdown.innerHTML = `
                    <div style="padding:15px; text-align:center; color:#c0b0a0; font-family:'Gaegu', cursive; font-size:14px;">
                        결과가 없어요! 😢
                    </div>`;
            } else {
                users.forEach(user => {
                    const item = document.createElement('div');
                    item.className = 'search-item';

                    // 클릭 시 파도타기 이동
                    item.onclick = () => location.href = `/?host_id=${user.pk}`;

                    item.innerHTML = `
                        <div class="search-item-title">
                            ${user.nick} 
                            <span style="font-weight:normal; font-size:12px; color:#ff7675;">
                                (${user.name})
                            </span>
                        </div>
                        <div class="search-item-desc">🏠 ${user.title}</div>
                    `;

                    searchDropdown.appendChild(item);
                });
            }
            searchDropdown.classList.remove('hidden');
        }

        // 다른 곳 클릭하면 드롭다운 닫기
        document.addEventListener('click', function (e) {
            if (!searchInput.contains(e.target) && !searchDropdown.contains(e.target)) {
                searchDropdown.classList.add('hidden');
            }
        });
    }
});