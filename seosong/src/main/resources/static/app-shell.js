(function () {
    const page = window.location.pathname.split('/').pop() || 'listar.html';
    const links = [
        ['listar.html', 'Usuários'],
        ['musicas.html', 'Músicas'],
        ['ia.html', 'IA'],
        ['cadastro-user.html', 'Novo usuário'],
        ['cadastro-artista.html', 'Novo artista'],
        ['cadastro-musica.html', 'Nova música'],
        ['cadastro-playlist.html', 'Playlist']
    ];

    const nav = document.createElement('nav');
    nav.className = 'app-nav';
    nav.innerHTML = `
        <a class="brand" href="listar.html" aria-label="SeoSong, início">
            <span class="brand-mark">♪</span> SeoSong
        </a>
        <div class="app-nav-links">
            ${links.map(([href, label], index) => `
                <a class="app-nav-link ${page === href ? 'active' : ''} ${href === 'ia.html' ? 'primary' : ''}" href="${href}">${label}</a>
            `).join('')}
        </div>
    `;

    document.body.classList.add('app-body');
    document.body.prepend(nav);
})();