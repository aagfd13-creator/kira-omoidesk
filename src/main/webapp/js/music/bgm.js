// bgm.js — index.jsp에 include된 bgm.jsp용
// player.js와 같은 window를 공유하므로 직접 접근 가능

function formatTime(sec) {
    const m = Math.floor(sec / 60);
    const s = Math.floor(sec % 60);
    return m + ':' + String(s).padStart(2, '0');
}

function updateNowPlaying(track, index) {
    if (!track) return;
    const nowThumb = document.getElementById('now-thumb');
    const nowTitle = document.getElementById('now-title');
    if (nowThumb) nowThumb.src = 'https://img.youtube.com/vi/' + track.youtubeId + '/mqdefault.jpg';
    if (nowTitle) nowTitle.textContent = track.title;

    document.querySelectorAll('.bgm-track-item').forEach((el, i) => {
        el.classList.toggle('active', i === index);
    });
}

function renderQueue() {
    // ✅ 같은 window이므로 직접 접근
    if (!window.playlist || window.playlist.length === 0) {
        setTimeout(renderQueue, 300);
        return;
    }

    const container = document.getElementById('bgm-queue-list');
    if (!container) return;

    container.innerHTML = '';
    window.playlist.forEach((track, i) => {
        const item = document.createElement('div');
        item.className = 'bgm-track-item' + (i === window.currentIndex ? ' active' : '');
        item.innerHTML = `
    <div class="bgm-track-num">${i + 1}</div>
    <div class="bgm-playing-icon">
        <span></span><span></span><span></span>
    </div>
    <div class="bgm-track-thumb">
    <img src="https://img.youtube.com/vi/${track.youtubeId}/mqdefault.jpg" alt="${track.title}">
</div>
    <div class="bgm-track-info">
        <div class="bgm-track-title">${track.title}</div>
        <div class="bgm-track-duration">${formatTime(track.duration)}</div>
    </div>
`;
        item.addEventListener('click', () => {
            if (typeof window.playTrack === 'function') window.playTrack(i);
        });
        container.appendChild(item);
    });

    updateNowPlaying(window.playlist[window.currentIndex], window.currentIndex);
}

// ✅ player.js가 곡을 바꿀 때 호출하는 콜백
window.onTrackChanged = function(index) {
    updateNowPlaying(window.playlist[index], index);
};

document.addEventListener('DOMContentLoaded', renderQueue);

setTimeout(() => {
    renderQueue();
}, 0);